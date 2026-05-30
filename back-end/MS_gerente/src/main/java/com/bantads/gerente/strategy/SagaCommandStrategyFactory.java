package com.bantads.gerente.strategy;

import com.bantads.gerente.strategy.strategies.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired private SagaIncrementGerenteStrategy incrementClienteStrategy;
    @Autowired private SagaDecrementGerenteStrategy decrementClienteStrategy;
    @Autowired private SagaGetGerenteStrategy getGerenteStrategy;
    @Autowired private SagaCreateGerenteStrategy createGerenteStrategy;
    @Autowired private SagaUpdateGerenteStrategy updateGerenteStrategy;
    @Autowired private SagaDeletarGerenteStrategy deletarGerenteStrategy;

    public SagaCommandStrategy newCommand(String commandType) {
        return switch(commandType) {
            case "IncrementClientesGerente" -> incrementClienteStrategy;
            case "DecrementClientesGerente" -> decrementClienteStrategy;
            case "GetGerente" -> getGerenteStrategy;
            case "CreateGerente" -> createGerenteStrategy;
            case "AtualizarGerente" -> updateGerenteStrategy;
            case "DeletarGerente" -> deletarGerenteStrategy;
            default -> throw new IllegalStateException("Unexpected value: " + commandType);
        };
    }

}
