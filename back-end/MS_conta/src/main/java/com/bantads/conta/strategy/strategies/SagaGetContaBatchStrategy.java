package com.bantads.conta.strategy.strategies;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bantads.conta.dto.ContaDTO;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaGetContaBatchStrategy implements SagaCommandStrategy {

    @Autowired
    private ContaService contaService;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            var dto = mapper.readValue(cmd.payload(), new TypeReference<List<String>>(){});

            return contaService.findByCpf(dto).stream().map(ContaDTO::from).collect(Collectors.toMap(c -> c.cpf(), c->c));
        } catch (Exception ex) {
            throw ex;
        }
    }
}
