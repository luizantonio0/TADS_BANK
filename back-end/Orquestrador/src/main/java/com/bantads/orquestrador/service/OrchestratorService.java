package com.bantads.orquestrador.service;

import com.bantads.orquestrador.model.Orchestration;
import com.bantads.shared.dto.*;
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

    public void orchestrate(OrchestrationRequestDTO dto) {

        var orchestration = new Orchestration(
                dto.uuid(),
                dto.commands().size(),
                false,
                false,
                dto.autoConfirm(),
                new HashMap<>(),
                dto.commands(),
                new HashMap<>()
        );

        var orchestrationKey = "orchestration:" + orchestration.getId().toString();

        redisTemplate.opsForValue().set(orchestrationKey, orchestration);

        for(var cmd : orchestration.getCommands()) {
            String queueName = cmd.serviceName() + ".command";
            rabbitTemplate.convertAndSend(queueName, cmd);
        }

    }

    private void confirm(UUID orchestrationId, Map<String, String> errors, boolean ok) {
        rabbitTemplate.convertAndSend("orchestration.confirm", "orchestration.confirm", new OrchestrationConfirmDTO(orchestrationId, String.join(", ", errors.values()),  ok));
    }

}
