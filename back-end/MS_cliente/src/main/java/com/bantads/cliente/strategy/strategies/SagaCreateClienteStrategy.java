package com.bantads.cliente.strategy.strategies;

import com.bantads.cliente.dto.ClienteRequestDTO;
import com.bantads.cliente.dto.saga.output.CreateClienteOutputDTO;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaCreateClienteStrategy implements SagaCommandStrategy {

    @Autowired private RedisTemplate<String, String> redisTemplate;
    @Autowired private ClienteService clienteService;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {

        try {
            ObjectMapper mapper = new ObjectMapper();
            ClienteRequestDTO dto = mapper.readValue(cmd.payload(), ClienteRequestDTO.class);

            clienteService.cadastrarCliente(dto);
            redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:credentials", dto.cpf());

            return new CreateClienteOutputDTO(dto.cpf());
        } catch (IllegalArgumentException ex) {
            throw ex;
        }

    }

}
