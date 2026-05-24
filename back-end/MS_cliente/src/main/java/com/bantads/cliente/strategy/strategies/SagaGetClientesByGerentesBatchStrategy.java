package com.bantads.cliente.strategy.strategies;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bantads.cliente.dto.ClienteDTO;
import com.bantads.cliente.exception.HttpException;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaGetClientesByGerentesBatchStrategy implements SagaCommandStrategy {

    @Autowired private ClienteService clienteService;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws HttpException {
        ObjectMapper mapper = new ObjectMapper();
        var dto = mapper.readValue(cmd.payload(), new TypeReference<List<String>>() {});

        var response = new HashMap<String, List<ClienteDTO>>();
        for(var gerente : dto) {
            var clientes = clienteService.findByGerente(gerente);
            response.put(gerente, clientes.stream().map(ClienteDTO::from).toList());
        }

        return response;
    }
}
