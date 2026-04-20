package com.bantads.conta.consumer;

import com.bantads.conta.dto.orchestrator.OrchestrationCommandDTO;
import com.bantads.conta.dto.orchestrator.OrchestrationConfirmDTO;
import com.bantads.conta.dto.orchestrator.OrchestrationRequestDTO;
import com.bantads.conta.dto.orchestrator.OrchestrationResultDTO;
import com.bantads.conta.strategy.SagaCommandStrategy;
import com.bantads.conta.strategy.SagaCommandStrategyFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitConsumer {

    @Autowired
    private SagaCommandStrategyFactory cmdFactory;

    @RabbitListener(queues = "ms-conta.command")
    public <T> OrchestrationResultDTO consumeCreate(OrchestrationCommandDTO<T> dto) {
        var strategy = (SagaCommandStrategy<T>) cmdFactory.newCommand(dto.commandType());
        if (strategy == null) {
            return new OrchestrationResultDTO(
                    dto.idCommand(),
                    dto.idOrchestration(),
                    "None strategy found for command " + dto.commandType(),
                    false,
                    null
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

}
