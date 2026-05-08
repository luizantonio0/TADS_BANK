package com.bantads.conta.strategy;

import com.bantads.conta.orchestration.OrchestrationKeys;
import com.bantads.conta.strategy.strategies.SagaCreateContaStrategy;
import com.bantads.conta.strategy.strategies.SagaUpdateLimiteStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired private SagaCreateContaStrategy createContaStrategy;
    @Autowired private SagaUpdateLimiteStrategy updateLimiteStrategy;

    public SagaCommandStrategy newCommand(String commandType) {
        return switch(commandType) {
            case OrchestrationKeys.CREATE_CONTA_COMMAND -> createContaStrategy;
            case OrchestrationKeys.UPDATE_LIMITE_COMMAND -> updateLimiteStrategy;
            default -> null;
        };
    }

}
