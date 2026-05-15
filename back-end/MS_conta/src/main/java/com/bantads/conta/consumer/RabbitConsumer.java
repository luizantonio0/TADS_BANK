package com.bantads.conta.consumer;

import com.bantads.shared.dto.*;
import com.bantads.conta.exception.HttpException;
import com.bantads.conta.orchestration.OrchestrationKeys;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.strategy.SagaCommandStrategyFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class RabbitConsumer {

    @Autowired private SagaCommandStrategyFactory cmdFactory;

    @Autowired private ContaService contaService;

    @Autowired private RedisTemplate<String, String> redisTemplate;

    @Autowired private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "ms-conta.orchestration.finished")
    public void onFinished(OrchestrationRequestResultDTO dto) {

    }

    @RabbitListener(queues = OrchestrationKeys.MS_CONTA + ".command")
    public void onCommand(OrchestrationCommandDTO dto) {
        var strategy = cmdFactory.newCommand(dto.commandType());
        var objectMapper = new ObjectMapper();
        if (strategy == null) {
            rabbitTemplate.convertAndSend("orchestration.result", new OrchestrationCommandResultDTO(
                    dto.idCommand(),
                    dto.idOrchestration(),
                    "ms-conta",
                    new OrchestrationErrorDTO(500, "Nenhuma estratégia para o comando " + dto.commandType()),
                    null
            ));
            return;
        }

        String payload = null;
        OrchestrationErrorDTO message = null;

        try {
            var obj = strategy.handle(dto);
            if(obj != null) {
                payload = objectMapper.writeValueAsString(obj);
            }
        } catch (HttpException ex) {
            message = new OrchestrationErrorDTO(ex.getStatusCode(), ex.getMessage());
        } catch (Exception ex) {
            message = new OrchestrationErrorDTO(500, ex.getMessage());
        }

        rabbitTemplate.convertAndSend("orchestration.result", new OrchestrationCommandResultDTO(
                dto.idCommand(),
                dto.idOrchestration(),
                "ms-conta",
                message,
                payload
        ));
    }

    @RabbitListener(queues = "ms-conta.orchestration.confirm")
    public void onConfirm(OrchestrationConfirmDTO dto) {

        var redisKey = dto.idOrchestration().toString() + ":touched:conta";
        var touched = redisTemplate.opsForValue().getAndDelete(redisKey);

        if(dto.ok() || touched == null) {
            return;
        }

        try {
            contaService.rollbackConta(UUID.fromString(touched));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
