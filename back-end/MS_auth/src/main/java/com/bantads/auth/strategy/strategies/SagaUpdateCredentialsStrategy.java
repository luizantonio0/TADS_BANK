package com.bantads.auth.strategy.strategies;

import com.bantads.auth.dto.CredentialsUpdateInputDTO;
import com.bantads.auth.dto.orchestration.OrchestrationCommandDTO;
import com.bantads.auth.exception.CredentialsAlreadyExistsException;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.strategy.SagaCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class SagaUpdateCredentialsStrategy implements SagaCommandStrategy<CredentialsUpdateInputDTO> {

    @Autowired
    private AuthService authService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Object handle(OrchestrationCommandDTO<CredentialsUpdateInputDTO> cmd) throws CredentialsAlreadyExistsException, IllegalArgumentException {
        try {
            authService.updateCredentials(cmd.dto().cpf(), cmd.dto().email());
            redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:credentials", cmd.dto().cpf());
        } catch (CredentialsAlreadyExistsException | IllegalArgumentException ex) {
            throw ex;
        }
        return null;
    }

}
