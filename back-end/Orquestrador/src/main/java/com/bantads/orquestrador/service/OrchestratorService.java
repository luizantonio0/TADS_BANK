package com.bantads.orquestrador.service;

import com.bantads.orquestrador.dto.*;
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

    public OrchestrationRequestResultDTO orchestrate(OrchestrationRequestDTO dto) {

        var orchestration = new Orchestration(
                dto.uuid(),
                false,
                new HashMap<>(),
                dto.commands().stream().map(OrchestrationCommandDTO::toCommand).collect(Collectors.toList()),
                new HashMap<>()
        );

        redisTemplate.opsForValue().set(orchestration.id().toString(), orchestration);

        CountDownLatch latch = new CountDownLatch(orchestration.commands().size());
        var errors = new HashMap<String, String>();

        for(var cmd : orchestration.commands()) {
            String queueName = cmd.targetService() + ".command";
            waitResultAsync(dto.uuid(), queueName, cmd, latch);
        }

        try {
            // 15 segundo no maximo para todos os serviços retornarem ok ou erro.
            var ok = latch.await(15, TimeUnit.SECONDS);
            if(!ok) {
                orchestration.getErrors().put("orquestrador", "Tempo de espera excedido");
                orchestration.setFailed(true);
            }
            orchestration = redisTemplate.opsForValue().get(dto.uuid());
            if(orchestration == null) {
                // por algum motivo nao existe mais a orchestration no redis ???
                orchestration.getErrors().put("orquestrador", "Orquestrador não existe mais no registro");
                orchestration.setFailed(true);
            }
            confirm(dto.uuid(), orchestration.getErrors(), orchestration.failed());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return new OrchestrationRequestResultDTO(dto.uuid(), orchestration.failed(), orchestration.getPayloads(), orchestration.getErrors());
    }

    private void confirm(UUID orchestrationId, Map<String, String> errors, boolean ok) {
        rabbitTemplate.convertAndSend("orchestration.confirm", new OrchestrationConfirmDTO(orchestrationId, String.join(", ", errors.values()),  ok));
    }

    @Async
    public <T> void waitResultAsync(UUID idOrchestration, String queue, Command<?> dto, CountDownLatch latch) {

        RLock lock = redisson.getLock("lock:" + idOrchestration.toString());

        try {
            if (lock.tryLock()) {
                try {
                    var orchestration = redisTemplate.opsForValue().get(idOrchestration.toString());
                    if(orchestration == null) {
                        throw new IllegalStateException("Orquestração não existe no Redis");
                    }
                    var result = (OrchestrationCommandResultDTO) rabbitTemplate.convertSendAndReceive(queue, dto);
                    latch.countDown();
                    if(result == null) {
                        orchestration.setFailed(true);
                        orchestration.getErrors().put(dto.targetService(), "O serviço retornou nulo");
                    } else {
                        var payload = result.payload() == null ? "" : result.payload();
                        orchestration.getPayloads().put(dto.targetService(), payload);
                        if(!orchestration.failed() && result.ok() != orchestration.failed()) {
                            orchestration.setFailed(result.ok());
                            orchestration.getErrors().put(dto.targetService(), result.message());
                            redisTemplate.opsForValue().set(result.idOrchestration().toString(), orchestration);
                        }
                    }
                    redisTemplate.opsForValue().set(idOrchestration.toString(), orchestration);
                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
