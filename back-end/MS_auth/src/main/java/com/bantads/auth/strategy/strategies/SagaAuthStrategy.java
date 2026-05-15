package com.bantads.auth.strategy.strategies;

import com.bantads.shared.dto.*;
import com.bantads.auth.dto.LoginDTO;
import com.bantads.auth.dto.saga.AuthResponseDTO;
import com.bantads.auth.exception.UnauthorizedException;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.service.JwtService;
import com.bantads.auth.strategy.SagaCommandStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

@Component
public class SagaAuthStrategy implements SagaCommandStrategy {

    @Autowired private AuthService authService;
    @Autowired private JwtService jwtService;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) throws UnauthorizedException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            LoginDTO dto = mapper.readValue(cmd.payload(), LoginDTO.class);
            var claims = authService.auth(dto.login(), dto.senha());
            
            if(claims == null) {
                throw new UnauthorizedException("Credenciais inválidas");
            }

            var token = jwtService.generateToken(claims.cpf(), claims.profile());
            
            jwtService.revokeAllTokens(claims.cpf());
            jwtService.saveToken(claims.cpf(), token);

            return new AuthResponseDTO(token, "bearer", claims.profile());
        } catch (Exception ex) {
            throw ex;
        }
    }

}
