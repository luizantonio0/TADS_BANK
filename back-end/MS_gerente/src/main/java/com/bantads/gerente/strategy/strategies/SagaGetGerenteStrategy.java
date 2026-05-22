package com.bantads.gerente.strategy.strategies;

import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

import org.springframework.stereotype.Component;

@Component
public class SagaGetGerenteStrategy implements SagaCommandStrategy {

    private final GerenteService gerenteService;

    public SagaGetGerenteStrategy(GerenteService gerenteService) {
        this.gerenteService = gerenteService;
    }

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        var cpf = cmd.payload();
        var gerente = gerenteService.findByCpf(cpf);

        return GerenteDTO.from(gerente);
    }
}
