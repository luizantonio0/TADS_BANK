package com.bantads.conta.consumer;

import com.bantads.conta.dto.orchestrator.OrchestrationCommandDTO;
import com.bantads.conta.dto.orchestrator.OrchestrationCommandResultDTO;
import com.bantads.conta.dto.orchestrator.OrchestrationConfirmDTO;
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
    public <T> OrchestrationCommandResultDTO consumeCreate(OrchestrationCommandDTO<T> dto) {
        var strategy = (SagaCommandStrategy<T>) cmdFactory.newCommand(dto.commandType());
        var objectMapper = new ObjectMapper();
        if (strategy == null) {
            return new OrchestrationCommandResultDTO(
                    dto.idCommand(),
                    dto.idOrchestration(),
                    "None strategy found for command " + dto.commandType(),
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

    @RabbitListener(queues = OrchestrationKeys.CONFIRM_QUEUE)
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
