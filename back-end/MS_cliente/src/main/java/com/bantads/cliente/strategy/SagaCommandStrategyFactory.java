package com.bantads.cliente.strategy;

import com.bantads.cliente.strategy.strategies.SagaAprovarClienteStrategy;
import com.bantads.cliente.strategy.strategies.SagaAtualizarClienteStrategy;
import com.bantads.cliente.strategy.strategies.SagaCreateClienteStrategy;
import com.bantads.cliente.strategy.strategies.SagaGetClienteBatchStrategy;
import com.bantads.cliente.strategy.strategies.SagaGetClienteStrategy;
import com.bantads.cliente.strategy.strategies.SagaGetClientesByGerentesBatchStrategy;
import com.bantads.cliente.strategy.strategies.SagaRejeitarClienteStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired private SagaCreateClienteStrategy createClienteStrategy;
    @Autowired private SagaAprovarClienteStrategy aprovarClienteStrategy;
    @Autowired private SagaRejeitarClienteStrategy rejeitarClienteStrategy;
    @Autowired private SagaAtualizarClienteStrategy atualizarClienteStrategy;
    @Autowired private SagaGetClienteBatchStrategy getClienteBatchStrategy;
    @Autowired private SagaGetClienteStrategy getClienteStrategy;
    @Autowired private SagaGetClientesByGerentesBatchStrategy getClienteByGerenteBatchStrategy;

    public SagaCommandStrategy newCommand(String commandType) {
        return switch(commandType) {
            case "CreateCliente" -> createClienteStrategy;
            case "ApproveCliente" -> aprovarClienteStrategy;
            case "RejectCliente" -> rejeitarClienteStrategy;
            case "UpdateCliente" -> atualizarClienteStrategy;
            case "GetCliente" -> getClienteStrategy;
            case "GetClienteBatch" -> getClienteBatchStrategy;
            case "GetClientesByGerentesBatch" -> getClienteByGerenteBatchStrategy;
            default -> null;
        };
    }

}
