package com.bantads.conta.strategy.strategies;

import com.bantads.conta.dto.ContaCreateOutputDTO;
import com.bantads.conta.dto.ContaCreateInputDTO;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@Component
public class SagaCreateContaStrategy implements SagaCommandStrategy {

    @Autowired
    private ContaService contaService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ContaCreateInputDTO dto = mapper.readValue(cmd.payload(), ContaCreateInputDTO.class);

            var conta = contaService.createConta(dto);
            redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:conta", conta.getId().toString());
            return new ContaCreateOutputDTO(conta.getCpf(), conta.getConta(), new BigDecimal(0), conta.getLimite());
        } catch (Exception ex) {
            throw ex;
        }
    }
}
