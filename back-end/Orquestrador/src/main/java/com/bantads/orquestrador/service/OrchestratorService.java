package com.bantads.orquestrador.service;

import com.bantads.orquestrador.dto.OrchestrationCommandDTO;
import com.bantads.orquestrador.dto.OrchestrationConfirmDTO;
import com.bantads.orquestrador.dto.OrchestrationRequestDTO;
import com.bantads.orquestrador.dto.OrchestrationResultDTO;
import com.bantads.orquestrador.model.Orchestration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrchestratorService {

    private static Map<UUID, CountDownLatch> latches = new HashMap<>();
    private final RedisTemplate<String, Orchestration> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public OrchestratorService(RedisTemplate<String, Orchestration> redisTemplate, RabbitTemplate rabbit) {
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbit;
    }

    public boolean orchestrate(OrchestrationRequestDTO dto) {

        var orchestration = new Orchestration(
                dto.uuid(),
                dto.commands().stream().map(OrchestrationCommandDTO::toCommand).collect(Collectors.toList())
        );

        redisTemplate.opsForValue().set(orchestration.id().toString(), orchestration);

        CountDownLatch latch = new CountDownLatch(orchestration.commands().size());
        latches.put(orchestration.id(), latch);

        for(var cmd : orchestration.commands()) {
            String queueName = cmd.targetService() + ".command";
            rabbitTemplate.convertAndSend(queueName, cmd);
        }

        try {
            // 15 segundo no maximo para todos os serviços retornarem ok ou erro.
            var ok = latch.await(15, TimeUnit.SECONDS);
            if(!ok) {
                confirm(dto.uuid(), false);
            }
            orchestration = redisTemplate.opsForValue().get(dto.uuid());
            if(orchestration == null) {
                // por algum motivo nao existe mais a orchestration no redis ???
                confirm(dto.uuid(), false);
            }
            confirm(dto.uuid(), orchestration.failed());
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return true;
    }

    private void confirm(UUID orchestrationId, boolean ok) {
        var confirm = new OrchestrationConfirmDTO(orchestrationId, ok);
        rabbitTemplate.convertAndSend("orchestration.confirm", confirm);
    }

    public void onResult(OrchestrationResultDTO dto) {
        var orchestration = redisTemplate.opsForValue().get(dto.idOrchestration().toString());
        if(orchestration != null) {
            if(!orchestration.failed() && dto.ok() != orchestration.failed()) {
                orchestration.setFailed(dto.ok());
                redisTemplate.opsForValue().set(dto.idOrchestration().toString(), orchestration);
            }
        }
    }
}
