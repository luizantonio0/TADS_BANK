package com.bantads.cliente.strategy.strategies;

import com.bantads.cliente.dto.saga.input.RejeitarClienteInputDTO;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.service.EmailService;
import com.bantads.cliente.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaRejeitarClienteStrategy implements SagaCommandStrategy {

    @Autowired private RedisTemplate<String, String> redisTemplate;
    @Autowired private ClienteService clienteService;
    @Autowired private EmailService emailService;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        var mapper = new ObjectMapper();
        var dto = mapper.readValue(cmd.payload(), RejeitarClienteInputDTO.class);

        var cli = clienteService.rejeitarCliente(dto.cpf(), dto.motivo());
        redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:cliente", cli.getId().toString());
        redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:logstatuscliente", cli.getId().toString());

        emailService.sendEmail(cli.getEmail(), "Sentimos muito!", "Infelizmente seu cadastro no BANTADS foi rejeitado.\nMotivo: " + dto.motivo());

        return null;
    }

}