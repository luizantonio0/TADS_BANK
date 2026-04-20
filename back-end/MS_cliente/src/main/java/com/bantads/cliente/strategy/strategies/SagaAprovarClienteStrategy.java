package com.bantads.cliente.strategy.strategies;

import com.bantads.cliente.dto.AprovarClienteDTO;
import com.bantads.cliente.dto.orchestrator.OrchestrationCommandDTO;
import com.bantads.cliente.model.Cliente;
import com.bantads.cliente.service.ClienteService;
import com.bantads.cliente.strategy.SagaCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class SagaAprovarClienteStrategy implements SagaCommandStrategy<AprovarClienteDTO> {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void handle(OrchestrationCommandDTO<AprovarClienteDTO> cmd) throws Exception {
        Cliente s = clienteService.findByCpf(cmd.dto().cpf());
        if (s == null) {
            throw new Exception("Cliente não encontrado");
        }
        clienteService.aprovarCliente(cmd.dto().cpf());
        redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:cliente", s.getId().toString());
    }
}
