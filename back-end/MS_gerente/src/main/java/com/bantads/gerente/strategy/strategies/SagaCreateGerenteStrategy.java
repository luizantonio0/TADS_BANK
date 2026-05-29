package com.bantads.gerente.strategy.strategies;

import com.bantads.gerente.dto.GerenteDTO;
import com.bantads.gerente.dto.request.CriaGerenteDTO;
import com.bantads.gerente.dto.saga.CriaGerenteComClienteDTO;
import com.bantads.gerente.service.GerenteService;
import com.bantads.gerente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

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
        var mapper = new ObjectMapper();
        CriaGerenteComClienteDTO dto = mapper.readValue(cmd.payload(), CriaGerenteComClienteDTO.class);

        var clientes = dto.cpf() == null ? new ArrayList<String>() : List.of(dto.cpfCliente());
        var ger = gerenteService.novoGerente(new CriaGerenteDTO(dto.nome(), dto.email(), dto.cpf(), null, dto.tipo(), null), clientes);
        redisTemplate.opsForValue().set(
            cmd.idOrchestration().toString() + ":touched:gerente",
            ger.getId().toString()
        );

        return GerenteDTO.from(ger, false);
    }
}
