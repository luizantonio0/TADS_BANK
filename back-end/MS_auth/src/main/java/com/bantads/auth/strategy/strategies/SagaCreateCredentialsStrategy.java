package com.bantads.auth.strategy.strategies;

import com.bantads.auth.dto.CredentialsCreateInputDTO;
import com.bantads.auth.dto.orchestration.OrchestrationCommandDTO;
import com.bantads.auth.exception.CredentialsAlreadyExistsException;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.strategy.SagaCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaCreateCredentialsStrategy implements SagaCommandStrategy<CredentialsCreateInputDTO> {

    @Autowired
    private AuthService authService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Object handle(OrchestrationCommandDTO<CredentialsCreateInputDTO> cmd) throws CredentialsAlreadyExistsException, IllegalArgumentException {
        try {
            authService.createCredentials(cmd.dto().email(), cmd.dto().cpf(), cmd.dto().password());
            redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:credentials", cmd.dto().cpf());
        } catch (CredentialsAlreadyExistsException | IllegalArgumentException ex) {
            throw ex;
        }
        return null;
    }

}
