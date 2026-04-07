package com.bantads.cliente.strategy.strategies;

import com.bantads.cliente.dto.AprovarClienteDTO;
import com.bantads.cliente.dto.orchestrator.OrchestrationCommandDTO;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.strategy.SagaCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;

public class SagaAprovarClienteStrategy implements SagaCommandStrategy<AprovarClienteDTO> {

    @Autowired
    private ClienteService clienteService;

    @Override
    public void handle(OrchestrationCommandDTO<AprovarClienteDTO> cmd) throws Exception {
        clienteService.aprovarCliente(cmd.dto().cpf());
    }
}
