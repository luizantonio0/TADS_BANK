package com.bantads.auth.consumer;

import com.bantads.auth.dto.CredentialsCreateDTO;
import com.bantads.auth.dto.orchestration.OrchestrationCommandDTO;
import com.bantads.auth.dto.orchestration.OrchestrationResultDTO;
import com.bantads.auth.exception.CredentialsAlreadyExistsException;
import com.bantads.auth.orchestration.OrchestrationKeys;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.strategy.SagaCommandStrategy;
import com.bantads.auth.strategy.SagaCommandStrategyFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrchestrationConsumer {

    @Autowired
    private SagaCommandStrategyFactory cmdFactory;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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

}
