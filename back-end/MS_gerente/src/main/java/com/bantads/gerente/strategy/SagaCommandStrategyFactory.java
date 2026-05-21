package com.bantads.gerente.strategy;

import com.bantads.gerente.strategy.strategies.SagaCreateGerenteStrategy;
import com.bantads.gerente.strategy.strategies.SagaDecrementGerenteStrategy;
import com.bantads.gerente.strategy.strategies.SagaIncrementGerenteStrategy;
import com.bantads.gerente.strategy.strategies.SagaGetGerenteStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired private SagaIncrementGerenteStrategy incrementClienteStrategy;
    @Autowired private SagaDecrementGerenteStrategy decrementClienteStrategy;
    @Autowired private SagaGetGerenteStrategy getGerenteStrategy;
    @Autowired private SagaCreateGerenteStrategy createGerenteStrategy;

    public SagaCommandStrategy newCommand(String commandType) {
        return switch(commandType) {
            case "IncrementClientesGerente" -> incrementClienteStrategy;
            case "DecrementClientesGerente" -> decrementClienteStrategy;
            case "GetGerente" -> getGerenteStrategy;
            case "CreateGerente" -> createGerenteStrategy;
            default -> throw new IllegalStateException("Unexpected value: " + commandType);
        };
    }

}
