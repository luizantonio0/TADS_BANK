package com.bantads.gerente.strategy.strategies;

import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.saga.DeletarGerenteInputDTO;
import com.bantads.gerente.model.Gerente;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaDeletarGerenteStrategy implements SagaCommandStrategy {

    @Autowired private GerenteService gerenteService;
    @Autowired private RedisTemplate<String, String> redisTemplate;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        var mapper = new ObjectMapper();
        DeletarGerenteInputDTO dto = mapper.readValue(cmd.payload(), DeletarGerenteInputDTO.class);
      
        Gerente ger = gerenteService.findByCpf(dto.cpfGerente());
        String idsAlterados = ger.getId().toString();

        if(dto.cpfGerenteDestino() != null) {
          var destino = gerenteService.atribuirClientes(dto.cpfGerenteDestino(), ger.getClientes());
          idsAlterados += "," + destino.getId().toString();
        }

        gerenteService.deleteByCpf(dto.cpfGerente());

        redisTemplate.opsForValue().set(
                cmd.idOrchestration().toString() + ":touched:gerente",
                idsAlterados
        );

        return GerenteDTO.from(ger, false);
    }
}
