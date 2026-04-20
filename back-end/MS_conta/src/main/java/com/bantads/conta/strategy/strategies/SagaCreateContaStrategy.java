package com.bantads.conta.strategy.strategies;

import com.bantads.conta.dto.orchestrator.OrchestrationCommandDTO;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.strategy.SagaCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCreateContaStrategy implements SagaCommandStrategy<ContaCreateDTO> {

    @Autowired
    private ContaService contaService;

    @Override
    public void handle(OrchestrationCommandDTO<ContaCreateDTO> cmd) throws Exception {
        try {
            contaService.createConta(cmd.dto());
        } catch (Exception ex) {
            throw ex;
        }
    }
}
