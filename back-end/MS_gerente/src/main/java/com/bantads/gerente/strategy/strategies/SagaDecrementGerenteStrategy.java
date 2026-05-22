package com.bantads.gerente.strategy.strategies;

import com.bantads.gerente.dto.saga.DefinirGerenteOutputDTO;
import com.bantads.gerente.dto.saga.GetGerenteInputDTO;
import com.bantads.gerente.exception.HttpException;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaDecrementGerenteStrategy implements SagaCommandStrategy {

    @Autowired
    private GerenteService gerenteService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws HttpException {
        var mapper = new ObjectMapper();
        var dto = mapper.readValue(cmd.payload(), GetGerenteInputDTO.class);
        var gerente = gerenteService.findByCpf(dto.cpf());

        gerenteService.decrementarCliente(gerente.getId());

        redisTemplate.opsForValue().set(
                cmd.idOrchestration().toString() + ":touched:gerente",
                gerente.getId().toString());

        return null;
    }
}
