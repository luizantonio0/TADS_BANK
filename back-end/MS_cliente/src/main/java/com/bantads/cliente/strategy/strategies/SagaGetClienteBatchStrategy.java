package com.bantads.cliente.strategy.strategies;

import java.util.List;
import java.util.stream.Collectors;

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
public class SagaGetClienteBatchStrategy implements SagaCommandStrategy {

    @Autowired private ClienteService clienteService;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws HttpException {
        ObjectMapper mapper = new ObjectMapper();
        var dto = mapper.readValue(cmd.payload(), new TypeReference<List<String>>() {});

        return clienteService.findByCpf(dto).stream().map(ClienteDTO::from).collect(Collectors.toMap(c -> c.cpf(), c->c));
    }
}
