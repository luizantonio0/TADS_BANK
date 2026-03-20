package com.bantads.auth.consumer;

import com.bantads.auth.dto.CredentialsCreateDTO;
import com.bantads.auth.dto.CredentialsUpdateDTO;
import com.bantads.auth.exception.CredentialsAlreadyExistsException;
import com.bantads.auth.service.AuthService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CredentialsConsumer {

    private AuthService authService;

    public CredentialsConsumer(AuthService authService) {
        this.authService = authService;
    }

    @RabbitListener(queues = "ms_auth.credentials.create")
    public void consumeCreate(CredentialsCreateDTO dto) {
        System.out.println(dto.cpf());
        try {
            authService.createCredentials(dto.email(), dto.cpf(), dto.password());
        } catch (CredentialsAlreadyExistsException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    @RabbitListener(queues = "ms_auth.credentials.update")
    public void consumeUpdate(CredentialsUpdateDTO dto) {
        try {
            authService.updateCredentials(dto.cpf(), dto.email());
        } catch (CredentialsAlreadyExistsException | IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

}
