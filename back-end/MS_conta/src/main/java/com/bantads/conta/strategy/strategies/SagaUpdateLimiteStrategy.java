package com.bantads.conta.strategy.strategies;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.bantads.conta.dto.AtualizarLimiteInputDTO;
import com.bantads.conta.dto.ContaCreateOutputDTO;
import com.bantads.conta.service.ContaService;
import com.bantads.conta.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

import tools.jackson.databind.ObjectMapper;

@Component
public class SagaUpdateLimiteStrategy implements SagaCommandStrategy {

    @Autowired
    private ContaService contaService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        try {

            ObjectMapper mapper = new ObjectMapper();
            AtualizarLimiteInputDTO dto = mapper.readValue(cmd.payload(), AtualizarLimiteInputDTO.class);

            var conta = contaService.atualizarLimite(dto.cpf(), dto.salario());
            redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:conta", conta.getId().toString());
            return new ContaCreateOutputDTO(conta.getCpf(), conta.getConta(), new BigDecimal(0), conta.getLimite());
        } catch (Exception ex) {
            throw ex;
        }
    }
}
