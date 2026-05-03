package com.bantads.gerente.strategy;

import com.bantads.gerente.orchestration.OrchestrationKeys;
import com.bantads.gerente.strategy.strategies.SagaDefinirGerenteStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    private final SagaDefinirGerenteStrategy findGerenteStrategy;

    public SagaCommandStrategyFactory(SagaDefinirGerenteStrategy findGerenteStrategy) {
        this.findGerenteStrategy = findGerenteStrategy;
    }

    public SagaCommandStrategy newCommand(String commandType) {
        return switch(commandType) {
            case OrchestrationKeys.FIND_GERENTE_COMMAND -> findGerenteStrategy;
            default -> throw new IllegalStateException("Unexpected value: " + commandType);
        };
    }

}
