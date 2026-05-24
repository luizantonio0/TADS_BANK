package com.bantads.conta.strategy;

import com.bantads.conta.strategy.strategies.SagaCreateContaStrategy;
import com.bantads.conta.strategy.strategies.SagaGetContaBatchStrategy;
import com.bantads.conta.strategy.strategies.SagaGetContaStrategy;
import com.bantads.conta.strategy.strategies.SagaGetContasByGerentesBatchStrategy;
import com.bantads.conta.strategy.strategies.SagaGetMelhoresContasStrategy;
import com.bantads.conta.strategy.strategies.SagaUpdateLimiteStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired private SagaCreateContaStrategy createContaStrategy;
    @Autowired private SagaGetContaBatchStrategy getContaBatchStrategy;
    @Autowired private SagaGetContaStrategy getContaStrategy;
    @Autowired private SagaUpdateLimiteStrategy updateLimiteStrategy;
    @Autowired private SagaGetMelhoresContasStrategy getMelhoresContasStrategy;
    @Autowired private SagaGetContasByGerentesBatchStrategy getContasByGerenteBatchStrategy;

    public SagaCommandStrategy newCommand(String commandType) {
        return switch(commandType) {
            case "CreateConta" -> createContaStrategy;
            case "UpdateLimite" -> updateLimiteStrategy;
            case "GetContaBatch" -> getContaBatchStrategy;
            case "GetMelhoresContas" -> getMelhoresContasStrategy;
            case "GetConta" -> getContaStrategy;
            case "GetContasByGerentesBatch" -> getContasByGerenteBatchStrategy;
            default -> null;
        };
    }

}
