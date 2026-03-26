package com.bantads.auth.strategy.strategies;

import com.bantads.auth.dto.CredentialsUpdateDTO;
import com.bantads.auth.dto.orchestration.OrchestrationCommandDTO;
import com.bantads.auth.dto.orchestration.OrchestrationResultDTO;
import com.bantads.auth.exception.CredentialsAlreadyExistsException;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.strategy.SagaCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaUpdateCredentialsStrategy implements SagaCommandStrategy<CredentialsUpdateDTO> {

    @Autowired
    private AuthService authService;

    @Override
    public void handle(OrchestrationCommandDTO<CredentialsUpdateDTO> cmd) throws CredentialsAlreadyExistsException, IllegalArgumentException {
        try {
            authService.updateCredentials(cmd.dto().cpf(), cmd.dto().email());
        } catch (CredentialsAlreadyExistsException | IllegalArgumentException ex) {
            throw ex;
        }
    }

}
