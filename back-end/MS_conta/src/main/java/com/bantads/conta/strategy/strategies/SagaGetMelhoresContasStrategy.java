package com.bantads.conta.strategy.strategies;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bantads.conta.dto.ContaDTO;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

@Component
public class SagaGetMelhoresContasStrategy implements SagaCommandStrategy {

    @Autowired
    private ContaService contaService;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        try {
            return contaService.findMelhoresContas().stream().map(ContaDTO::from).collect(Collectors.toList());
        } catch (Exception ex) {
            throw ex;
        }
    }
}
