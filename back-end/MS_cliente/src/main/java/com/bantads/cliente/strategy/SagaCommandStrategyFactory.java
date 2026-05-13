package com.bantads.cliente.strategy;

import com.bantads.cliente.orchestration.OrchestrationKeys;
import com.bantads.cliente.strategy.strategies.SagaAprovarClienteStrategy;
import com.bantads.cliente.strategy.strategies.SagaAtualizarClienteStrategy;
import com.bantads.cliente.strategy.strategies.SagaCreateClienteStrategy;
import com.bantads.cliente.strategy.strategies.SagaGetClienteStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired private SagaCreateClienteStrategy createClienteStrategy;
    @Autowired private SagaAprovarClienteStrategy aprovarClienteStrategy;
    @Autowired private SagaAtualizarClienteStrategy atualizarClienteStrategy;
    @Autowired private SagaGetClienteStrategy getClienteStrategy;

    public SagaCommandStrategy newCommand(String commandType) {
        return switch(commandType) {
            case OrchestrationKeys.CREATE_CLIENTE_COMMAND -> createClienteStrategy;
            case OrchestrationKeys.APPROVE_CLIENTE_COMMAND -> aprovarClienteStrategy;
            case OrchestrationKeys.UPDATE_CLIENTE_COMMAND -> atualizarClienteStrategy;
            case OrchestrationKeys.GET_CLIENTE_COMMAND -> getClienteStrategy;
            default -> null;
        };
    }

}
