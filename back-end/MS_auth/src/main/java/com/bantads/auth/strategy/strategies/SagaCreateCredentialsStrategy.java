package com.bantads.auth.strategy.strategies;

import com.bantads.shared.dto.*;
import com.bantads.auth.dto.saga.CredentialsCreateInputDTO;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.service.EmailService;
import com.bantads.auth.strategy.SagaCommandStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SagaCreateCredentialsStrategy implements SagaCommandStrategy {

    @Autowired private AuthService authService;
    @Autowired private EmailService emailService;

    @Autowired private RedisTemplate<String, String> redisTemplate;

    @Autowired private PasswordEncoder encoder;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws Exception {
        try {

            ObjectMapper mapper = new ObjectMapper();
            CredentialsCreateInputDTO dto = mapper.readValue(cmd.payload(), CredentialsCreateInputDTO.class);

            authService.createCredentials(dto.email(), dto.cpf(), encoder.encode(dto.password()), dto.profile());
            redisTemplate.opsForValue().set(cmd.idOrchestration().toString() + ":touched:credentials", dto.cpf());
            
            //emailService.sendEmail(dto.email(), "Bem vindo ao BANTADS!", "Seja bem vindo ao banco mais tecnológico do mundo!\nUtilize a senha " + dto.password() + " para entrar na sua conta.");

        } catch (Exception ex) {
            throw ex;
        }
        return null;
    }

}
