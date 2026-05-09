package com.bantads.gerente.strategy;

import com.bantads.gerente.orchestration.OrchestrationKeys;
import com.bantads.gerente.strategy.strategies.SagaDefinirGerenteStrategy;
import com.bantads.gerente.strategy.strategies.SagaGetGerenteStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired private SagaDefinirGerenteStrategy findGerenteStrategy;
    @Autowired private SagaGetGerenteStrategy getGerenteStrategy;

    public SagaCommandStrategy newCommand(String commandType) {
        return switch(commandType) {
            case OrchestrationKeys.FIND_GERENTE_COMMAND -> findGerenteStrategy;
            case OrchestrationKeys.GET_GERENTE_COMMAND -> getGerenteStrategy;
            default -> throw new IllegalStateException("Unexpected value: " + commandType);
        };
    }

}
