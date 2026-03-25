package com.bantads.orquestrador.service;

import com.bantads.orquestrador.dto.OrchestrationCommandDTO;
import com.bantads.orquestrador.dto.OrchestrationRequestDTO;
import com.bantads.orquestrador.model.Command;
import com.bantads.orquestrador.model.Orchestration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class OrchestratorService {

    private static Map<UUID, CountDownLatch> latches = new HashMap<>();
    private final RedisTemplate<String, Orchestration> redisTemplate;

    public OrchestratorService(RedisTemplate<String, Orchestration> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean orchestrate(OrchestrationRequestDTO dto) {

        var orchestration = new Orchestration(
                dto.uuid(),
                dto.commands().stream().map(OrchestrationCommandDTO::toCommand).collect(Collectors.toList())
        );

        redisTemplate.opsForValue().set(orchestration.getIdOrchestration().toString(), orchestration);

        CountDownLatch latch = new CountDownLatch(orchestration.getCommands().size());
        latches.put(orchestration.getIdOrchestration(), latch);

        try {
            var ok = latch.await(10, TimeUnit.SECONDS);
            if(!ok) {
                // faz rollback
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        return true;
    }

    public boolean onResult()
}
