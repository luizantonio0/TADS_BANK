package com.bantads.conta.consumer;

import com.bantads.shared.dto.*;
import com.bantads.conta.orchestration.OrchestrationKeys;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.strategy.SagaCommandStrategy;
import com.bantads.conta.strategy.SagaCommandStrategyFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class RabbitConsumer {

    @Autowired
    private SagaCommandStrategyFactory cmdFactory;

    @Autowired
    private ContaService contaService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @RabbitListener(queues = OrchestrationKeys.MS_CONTA + ".command")
    public <T> OrchestrationCommandResultDTO consumeCreate(OrchestrationCommandDTO dto) {
        var strategy = cmdFactory.newCommand(dto.commandType());
        var objectMapper = new ObjectMapper();
        if (strategy == null) {
            return new OrchestrationCommandResultDTO(
                    dto.idCommand(),
                    dto.idOrchestration(),
                    "Nenhuma estratégia para o comando " + dto.commandType(),
                    false,
                    null
            );
        }

        String payload = null;
        String message = "";
        boolean ok = true;

        try {
            var obj = strategy.handle(dto);
            if(obj != null) {
                payload = objectMapper.writeValueAsString(obj);
            }
        } catch (Exception ex) {
            ok = false;
            message = ex.getMessage();
        }

        return new OrchestrationCommandResultDTO(dto.idCommand(), dto.idOrchestration(), message, ok, payload);
    }

    @RabbitListener(queues = "ms-conta.orchestration.confirm")
    public void consumeConfirm(OrchestrationConfirmDTO dto) {

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
