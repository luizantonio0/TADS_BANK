package com.bantads.gerente.strategy.strategies;

import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaCreateGerenteStrategy implements SagaCommandStrategy {

    @Autowired
    private GerenteService gerenteService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception{
        try {
            var mapper = new ObjectMapper();
            CriaGerenteDTO dto = mapper.readValue(cmd.payload(), CriaGerenteDTO.class);

            var ger = gerenteService.novoGerente(dto);
            redisTemplate.opsForValue().set(
                    cmd.idOrchestration().toString() + ":touched:gerente",
                    ger.getId().toString()
            );

            return GerenteDTO.from(ger);
        } catch (Exception ex) {
            throw ex;
        }
    }
}
