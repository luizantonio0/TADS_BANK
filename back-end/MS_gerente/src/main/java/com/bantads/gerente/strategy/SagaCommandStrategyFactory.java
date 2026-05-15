package com.bantads.gerente.strategy;

import com.bantads.gerente.strategy.strategies.SagaCreateGerenteStrategy;
import com.bantads.gerente.strategy.strategies.SagaDefinirGerenteStrategy;
import com.bantads.gerente.strategy.strategies.SagaGetGerenteStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired private SagaDefinirGerenteStrategy findGerenteStrategy;
    @Autowired private SagaGetGerenteStrategy getGerenteStrategy;
    @Autowired private SagaCreateGerenteStrategy createGerenteStrategy;

    public SagaCommandStrategy newCommand(String commandType) {
        return switch(commandType) {
            case "FindGerente" -> findGerenteStrategy;
            case "GetGerente" -> getGerenteStrategy;
            case "CreateGerente" -> createGerenteStrategy;
            default -> throw new IllegalStateException("Unexpected value: " + commandType);
        };
    }

}
