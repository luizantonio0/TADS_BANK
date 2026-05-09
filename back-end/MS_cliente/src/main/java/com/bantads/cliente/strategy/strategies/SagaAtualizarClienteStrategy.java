package com.bantads.cliente.strategy.strategies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.bantads.cliente.dto.http.AlterarDadosClienteDTO;
import com.bantads.cliente.dto.saga.input.AtualizarClienteInputDTO;
import com.bantads.cliente.dto.saga.output.AprovarClienteOutputDTO;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

import tools.jackson.databind.ObjectMapper;

@Component
public class SagaAtualizarClienteStrategy implements SagaCommandStrategy {
    @Autowired private RedisTemplate<String, String> redisTemplate;
    @Autowired private ClienteService clienteService;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {

        try {
            var mapper = new ObjectMapper();
            var dto = mapper.readValue(cmd.payload(), AtualizarClienteInputDTO.class);

            var cli = clienteService.update(new AlterarDadosClienteDTO(
                    dto.nome(),
                    dto.email(),
                    dto.salario(),
                    dto.endereco(),
                    dto.CEP(),
                    dto.cidade(),
                    dto.estado()
            ), dto.cpf());
            redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:cliente", cli.getId().toString());

            return new AprovarClienteOutputDTO(dto.cpf(), cli.getCriacao().toString());
        } catch (IllegalArgumentException ex) {
            throw ex;
        }

    }
}
