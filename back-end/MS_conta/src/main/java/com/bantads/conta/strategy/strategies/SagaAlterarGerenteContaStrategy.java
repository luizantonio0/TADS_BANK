package com.bantads.conta.strategy.strategies;

import com.bantads.conta.dto.AlterarGerenteDTO;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaAlterarGerenteContaStrategy implements SagaCommandStrategy {

    @Autowired
    private ContaService contaService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            AlterarGerenteDTO dto = mapper.readValue(cmd.payload(), AlterarGerenteDTO.class);

            var conta = contaService.findByCpf(dto.cpfCliente());
            conta.setCpfGerente(dto.cpfGerente());

            redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:conta", conta.getId().toString());
            return null;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
