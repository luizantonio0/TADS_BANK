package com.bantads.gerente.strategy.strategies;

import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.request.AtualizaGerenteDTO;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaUpdateGerenteStrategy implements SagaCommandStrategy {

    private final GerenteService gerenteService;
    private final RedisTemplate<String, String> redisTemplate;

    public SagaUpdateGerenteStrategy(GerenteService gerenteService, RedisTemplate<String, String> redisTemplate) {
        this.gerenteService = gerenteService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        var mapper = new ObjectMapper();
        AtualizaGerenteDTO _dto = mapper.readValue(cmd.payload(), AtualizaGerenteDTO.class);

        var dto = new AtualizaGerenteDTO(
            null,
                _dto.nome(),
                _dto.email(),
                _dto.telefone(),
                _dto.senha()
        );

        var gerente = gerenteService.updateByCpf(_dto.cpf(), dto);

        redisTemplate.opsForValue().set(
                cmd.idOrchestration().toString() + ":touched:gerente",
                gerente.getId().toString()
        );

        return GerenteDTO.from(gerente, false);
    }
}
