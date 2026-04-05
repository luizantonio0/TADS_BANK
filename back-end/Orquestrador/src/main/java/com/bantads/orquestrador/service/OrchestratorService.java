package com.bantads.orquestrador.service;

import com.bantads.orquestrador.dto.OrchestrationCommandDTO;
import com.bantads.orquestrador.dto.OrchestrationConfirmDTO;
import com.bantads.orquestrador.dto.OrchestrationRequestDTO;
import com.bantads.orquestrador.dto.OrchestrationResultDTO;
import com.bantads.orquestrador.model.Command;
import com.bantads.orquestrador.model.Orchestration;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrchestratorService {

    private final RedisTemplate<String, Orchestration> redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final RedissonClient redisson;

    public OrchestratorService(RedissonClient redisson, RedisTemplate<String, Orchestration> redisTemplate, RabbitTemplate rabbit) {
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbit;
        this.redisson = redisson;
    }

    public OrchestrationConfirmDTO orchestrate(OrchestrationRequestDTO dto) {

        var orchestration = new Orchestration(
                dto.uuid(),
                false,
                new ArrayList<>(),
                dto.commands().stream().map(OrchestrationCommandDTO::toCommand).collect(Collectors.toList())
        );

        redisTemplate.opsForValue().set(orchestration.id().toString(), orchestration);

        CountDownLatch latch = new CountDownLatch(orchestration.commands().size());

        for(var cmd : orchestration.commands()) {
            String queueName = cmd.targetService() + ".command";
            waitResultAsync(dto.uuid(), queueName, cmd, latch);
        }

        try {
            // 15 segundo no maximo para todos os serviços retornarem ok ou erro.
            var ok = latch.await(15, TimeUnit.SECONDS);
            if(!ok) {
                return confirm(dto.uuid(), List.of("Tempo de espera excedido"), false);
            }
            orchestration = redisTemplate.opsForValue().get(dto.uuid());
            if(orchestration == null) {
                // por algum motivo nao existe mais a orchestration no redis ???
                return confirm(dto.uuid(), List.of("Orquestração não existe mais no registro"), false);
            }
            return confirm(dto.uuid(), orchestration.getErrors(), orchestration.failed());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return confirm(dto.uuid(), List.of("Orquestração não existe mais no registro"), false);
    }

    private OrchestrationConfirmDTO confirm(UUID orchestrationId, List<String> errors, boolean ok) {
        var confirm = new OrchestrationConfirmDTO(orchestrationId, errors,  ok);
        rabbitTemplate.convertAndSend("orchestration.confirm", confirm);
        return confirm;
    }

    @Async
    public <T> void waitResultAsync(UUID idOrchestration, String queue, Command<?> dto, CountDownLatch latch) {

        RLock lock = redisson.getLock(idOrchestration.toString());

        try {
            if (lock.tryLock(3, 5, TimeUnit.SECONDS)) {
                try {
                    var orchestration = redisTemplate.opsForValue().get(idOrchestration.toString());
                    if(orchestration == null) {
                        throw new IllegalStateException("Orquestração não existe no Redis");
                    }
                    var result = (OrchestrationResultDTO) rabbitTemplate.convertSendAndReceive(queue, dto);
                    latch.countDown();
                    if(result == null) {
                        orchestration.setFailed(true);
                        orchestration.getErrors().add("O serviço retornou nulo");
                    } else {
                        if(!orchestration.failed() && result.ok() != orchestration.failed()) {
                            orchestration.setFailed(result.ok());
                            orchestration.getErrors().add(result.message());
                            redisTemplate.opsForValue().set(result.idOrchestration().toString(), orchestration);
                        }
                    }
                    redisTemplate.opsForValue().set(idOrchestration.toString(), orchestration);
                } finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
