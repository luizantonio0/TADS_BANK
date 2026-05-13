package com.bantads.cliente.strategy.strategies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bantads.cliente.dto.ClienteDTO;
import com.bantads.cliente.dto.saga.input.GetClienteInputDTO;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

import tools.jackson.databind.ObjectMapper;

@Component
public class SagaGetClienteStrategy implements SagaCommandStrategy {

    @Autowired private ClienteService clienteService;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {

        try {
            ObjectMapper mapper = new ObjectMapper();
            GetClienteInputDTO dto = mapper.readValue(cmd.payload(), GetClienteInputDTO.class);

            return ClienteDTO.from(clienteService.findByCpf(dto.cpf()));
        } catch (IllegalArgumentException ex) {
            throw ex;
        }

    }
}
