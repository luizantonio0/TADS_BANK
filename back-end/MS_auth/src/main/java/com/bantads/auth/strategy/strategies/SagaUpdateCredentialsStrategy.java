package com.bantads.auth.strategy.strategies;

import com.bantads.shared.dto.*;
import com.bantads.auth.dto.saga.CredentialsUpdateInputDTO;
import com.bantads.auth.exception.BadRequestException;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.strategy.SagaCommandStrategy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaUpdateCredentialsStrategy implements SagaCommandStrategy {

    private final AuthService authService;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder encoder;

    public SagaUpdateCredentialsStrategy(AuthService authService, RedisTemplate<String, String> redisTemplate, PasswordEncoder encoder) {
        this.authService = authService;
        this.redisTemplate = redisTemplate;
        this.encoder = encoder;
    }

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws BadRequestException {
        ObjectMapper mapper = new ObjectMapper();
        CredentialsUpdateInputDTO dto = mapper.readValue(cmd.payload(), CredentialsUpdateInputDTO.class);

        var creds = authService.updateCredentials(dto.cpf(), dto.email(), encoder.encode(dto.password()), dto.changePassword());
        redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:credentials", creds.getId());
        return null;
    }

}
