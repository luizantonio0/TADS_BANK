package com.bantads.cliente.strategy.strategies;

import com.bantads.cliente.dto.saga.input.AlterarGerenteDTO;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.*;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaAlterarGerenteClienteStrategy implements SagaCommandStrategy {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            AlterarGerenteDTO dto = mapper.readValue(cmd.payload(), AlterarGerenteDTO.class);

            List<String> idsAlterados = new ArrayList<>();
            for (var c : dto.cpfCliente().split(",")) {
                var cliente = clienteService.updateGerente(c, dto.cpfGerente());
                idsAlterados.add(cliente.getId().toString());
            }
            
            redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:cliente", String.join(",", idsAlterados));
            return null;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
