package com.bantads.cliente.consumer;

import com.bantads.cliente.dto.orchestrator.OrchestrationCommandDTO;
import com.bantads.cliente.dto.orchestrator.OrchestrationConfirmDTO;
import com.bantads.cliente.dto.orchestrator.OrchestrationResultDTO;
import com.bantads.cliente.orchestration.OrchestrationKeys;
import com.bantads.cliente.strategy.SagaCommandStrategy;
import com.bantads.cliente.strategy.SagaCommandStrategyFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrchestrationConsumer {

    @Autowired
    private SagaCommandStrategyFactory cmdFactory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @RabbitListener(queues = "ms-auth.command")
    public <T> OrchestrationResultDTO consumeCreate(OrchestrationCommandDTO<T> dto) {
        var strategy = (SagaCommandStrategy<T>) cmdFactory.newCommand(dto.commandType());
        if (strategy == null) {
            return new OrchestrationResultDTO(
                            dto.idCommand(),
                            dto.idOrchestration(),
                            "None strategy found for command " + dto.commandType(),
                            false
                    );
        }

        String message = "";
        boolean ok = true;

        try {
            strategy.handle(dto);
        } catch (Exception ex) {
            ok = false;
            message = ex.getMessage();
        }

        return new OrchestrationResultDTO(dto.idCommand(), dto.idOrchestration(), message, ok);
    }

    @RabbitListener(queues = OrchestrationKeys.CONFIRM_QUEUE)
    public void consumeConfirm(OrchestrationConfirmDTO dto) {

        var redisKey = dto.idOrchestration().toString() + ":touched:cliente";
        var touchedCliente = redisTemplate.opsForValue().getAndDelete(redisKey);

        if(dto.ok()) {
            return;
        }

        // TODO: fazer rollback usando o hibernate envers

    }

}
