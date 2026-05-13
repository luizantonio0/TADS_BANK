package com.bantads.gerente.strategy.strategies;

import com.bantads.gerente.dto.DefinirGerenteOutputDTO;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaDefinirGerenteStrategy implements SagaCommandStrategy {

    @Autowired
    private GerenteService gerenteService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Object handle(OrchestrationCommandDTO cmd){
        try {

            var gerente = gerenteService.findGerenteMenosClientes();
            if (gerente.isEmpty()) {
                throw new IllegalArgumentException("Nenhum gerente disponível");
            }

            redisTemplate.opsForValue().set(
                    cmd.idOrchestration().toString() + ":touched:gerente",
                    gerente.get().getId().toString()
            );

            var res = new DefinirGerenteOutputDTO(
                    gerente.get().getId(),
                    gerente.get().getNome()
            );
            return res;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
