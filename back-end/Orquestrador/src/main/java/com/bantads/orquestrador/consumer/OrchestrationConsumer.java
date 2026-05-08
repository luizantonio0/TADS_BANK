package com.bantads.orquestrador.consumer;

import com.bantads.orquestrador.model.Orchestration;
import com.bantads.shared.dto.OrchestrationCommandResultDTO;
import com.bantads.shared.dto.OrchestrationConfirmDTO;
import com.bantads.shared.dto.OrchestrationRequestResultDTO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class OrchestrationConsumer {


    @Autowired private RedisTemplate<String, Orchestration> redisTemplate;

    @Autowired private RabbitTemplate rabbitTemplate;

    @Autowired private RedissonClient redisson;

    @RabbitListener(queues = "orchestration.result")
    public void onCommandResult(OrchestrationCommandResultDTO dto) {
        var orchestrationKey = "orchestration:" + dto.idOrchestration().toString();
        var orchestration = redisTemplate.opsForValue().get(orchestrationKey);

        if (orchestration == null) {
            System.out.println("Orquestração não existe mais no Redis");
            return;
        }

        RLock lock = redisson.getLock("lock:" + orchestrationKey);
        var ended = orchestration.decrementLatchAndTest();

        try {
            if (lock.tryLock(5, TimeUnit.SECONDS)) {
                if(orchestration.isFinished()) {
                    return;
                }
                orchestration.getPayloads().put(dto.sourceService(), dto.payload());
                if(!dto.ok()) {
                    orchestration.setFailed(true);
                    orchestration.getErrors().put(dto.sourceService(), dto.message());
                }
                if (ended) {
                    orchestration.setFinished(true);
                    rabbitTemplate.convertAndSend(
                            "orchestration.finished",
                            "orchestration.finished",
                            new OrchestrationRequestResultDTO(
                                    dto.idOrchestration(),
                                    orchestration.isFailed(),
                                    orchestration.getPayloads(),
                                    orchestration.getErrors())
                    );
                    if(orchestration.isAutoConfirm()) {
                        rabbitTemplate.convertAndSend(
                                "orchestration.confirm",
                                "orchestration.confirm",
                                new OrchestrationConfirmDTO(
                                        dto.idOrchestration(),
                                        String.join(",", orchestration.getErrors().values()),
                                        !orchestration.isFailed()
                        ));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            redisTemplate.opsForValue().set(orchestrationKey, orchestration);
            lock.unlock();
        }
    }

}
