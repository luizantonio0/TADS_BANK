package com.bantads.gerente.strategy.strategies;

import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.saga.GetGerenteInputDTO;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaGetGerenteStrategy implements SagaCommandStrategy {

    private final GerenteService gerenteService;

    public SagaGetGerenteStrategy(GerenteService gerenteService) {
        this.gerenteService = gerenteService;
    }

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        var mapper = new ObjectMapper();
        var dto = mapper.readValue(cmd.payload(), GetGerenteInputDTO.class);

        var gerente = gerenteService.findByCpf(dto.cpf());

        return GerenteDTO.from(gerente);
    }
}
