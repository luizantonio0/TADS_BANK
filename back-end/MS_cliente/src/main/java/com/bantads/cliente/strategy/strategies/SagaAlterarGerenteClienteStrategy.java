package com.bantads.cliente.strategy.strategies;

import com.bantads.cliente.dto.saga.input.AlterarGerenteDTO;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.*;
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

            var cliente = clienteService.updateGerente(dto.cpfCliente(), dto.cpfGerente());

            redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:cliente", cliente.getId().toString());
            return null;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
