package com.bantads.orquestrador.service;

import com.bantads.orquestrador.model.Orchestration;
import com.bantads.shared.dto.*;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrchestratorService {

    private final RedisTemplate<String, Orchestration> redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public OrchestratorService(RedissonClient redisson, RedisTemplate<String, Orchestration> redisTemplate, RabbitTemplate rabbit) {
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbit;
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

        System.out.println("Setou key: " + orchestrationKey);
        redisTemplate.opsForValue().set(orchestrationKey, orchestration);

        for(var cmd : orchestration.getCommands()) {
            String queueName = cmd.serviceName() + ".command";
            System.out.println("Enviou para " + queueName);
            rabbitTemplate.convertAndSend(queueName, cmd);
        }

    }

}
