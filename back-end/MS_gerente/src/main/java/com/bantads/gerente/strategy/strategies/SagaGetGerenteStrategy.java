package com.bantads.gerente.strategy.strategies;

import com.bantads.gerente.dto.DefinirGerenteInputDTO;
import com.bantads.gerente.dto.DefinirGerenteOutputDTO;
import com.bantads.gerente.dto.GetGerenteInputDTO;
import com.bantads.gerente.dto.GetGerenteOutputDTO;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaGetGerenteStrategy implements SagaCommandStrategy {

    private final GerenteService gerenteService;

    private final RedisTemplate<String, String> redisTemplate;

    public SagaGetGerenteStrategy(GerenteService gerenteService, RedisTemplate<String, String> redisTemplate) {
        this.gerenteService = gerenteService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Object handle(OrchestrationCommandDTO cmd){
        try {

            var mapper = new ObjectMapper();
            var dto = mapper.readValue(cmd.payload(), GetGerenteInputDTO.class);

            var gerente = gerenteService.findByCpf(cmd.payload());
            if (gerente.isEmpty()) {
                throw new IllegalArgumentException("Nenhum gerente encontrado");
            }

            return new GetGerenteOutputDTO(gerente.get().getNome());

        } catch (Exception ex) {
            throw ex;
        }
    }
}
