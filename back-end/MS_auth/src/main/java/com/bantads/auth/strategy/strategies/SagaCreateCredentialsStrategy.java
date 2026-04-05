package com.bantads.auth.strategy.strategies;

import com.bantads.auth.dto.CredentialsCreateDTO;
import com.bantads.auth.dto.orchestration.OrchestrationCommandDTO;
import com.bantads.auth.dto.orchestration.OrchestrationResultDTO;
import com.bantads.auth.exception.CredentialsAlreadyExistsException;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.strategy.SagaCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCreateCredentialsStrategy implements SagaCommandStrategy<CredentialsCreateDTO> {

    @Autowired
    private AuthService authService;

    @Override
    public void handle(OrchestrationCommandDTO<CredentialsCreateDTO> cmd) throws CredentialsAlreadyExistsException, IllegalArgumentException {
        try {
            authService.createCredentials(cmd.dto().email(), cmd.dto().cpf(), cmd.dto().password());
        } catch (CredentialsAlreadyExistsException | IllegalArgumentException ex) {
            throw ex;
        }
    }

}
