package com.bantads.auth.strategy.strategies;

import com.bantads.auth.dto.CredentialsUpdateInputDTO;
import com.bantads.shared.dto.*;
import com.bantads.auth.exception.CredentialsAlreadyExistsException;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.strategy.SagaCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaUpdateCredentialsStrategy implements SagaCommandStrategy {

    @Autowired
    private AuthService authService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws CredentialsAlreadyExistsException, IllegalArgumentException {
        try {

            ObjectMapper mapper = new ObjectMapper();
            CredentialsUpdateInputDTO dto = mapper.readValue(cmd.payload(), CredentialsUpdateInputDTO.class);

            authService.updateCredentials(dto.cpf(), dto.email());
            redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:credentials", dto.cpf());

        } catch (CredentialsAlreadyExistsException | IllegalArgumentException ex) {
            throw ex;
        }
        return null;
    }

}
