package com.bantads.gerente.strategy.strategies;

import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.saga.GetGerenteInputDTO;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaGetGerenteStrategy implements SagaCommandStrategy {

    @Autowired private GerenteService gerenteService;

    @Override
    public Object handle(OrchestrationCommandDTO cmd){
        try {

            var mapper = new ObjectMapper();
            var dto = mapper.readValue(cmd.payload(), GetGerenteInputDTO.class);

            var gerente = gerenteService.findByCpf(dto.cpf());
            if (gerente.isEmpty()) {
                throw new IllegalArgumentException("Nenhum gerente ou administrador encontrado");
            }

            return GerenteDTO.from(gerente.get());

        } catch (Exception ex) {
            throw ex;
        }
    }
}
