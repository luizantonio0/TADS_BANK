package com.bantads.auth.strategy;

import com.bantads.auth.orchestration.OrchestrationKeys;
import com.bantads.auth.strategy.strategies.SagaCreateCredentialsStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired
    private SagaCreateCredentialsStrategy createCredentialsStrategy;

    public <T> SagaCommandStrategy<T> newCommand(String commandType) {
        return (SagaCommandStrategy<T>) switch(commandType) {
            case OrchestrationKeys.CREATE_CREDENTIALS_COMMAND -> createCredentialsStrategy;
            default -> throw new IllegalStateException("Unexpected value: " + commandType);
        };
    }

}
