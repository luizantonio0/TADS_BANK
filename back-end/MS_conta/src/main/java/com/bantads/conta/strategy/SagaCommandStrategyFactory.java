package com.bantads.conta.strategy;

import com.bantads.conta.orchestration.OrchestrationKeys;
import com.bantads.conta.strategy.strategies.SagaCreateContaStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired
    private SagaCreateContaStrategy createContaStrategy;

    public <T> SagaCommandStrategy<T> newCommand(String commandType) {
        return (SagaCommandStrategy<T>) switch(commandType) {
            case OrchestrationKeys.CREATE_CONTA_COMMAND -> createContaStrategy;
            default -> throw new IllegalStateException("Unexpected value: " + commandType);
        };
    }

}
