package com.bantads.auth.strategy.strategies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bantads.auth.service.JwtService;
import com.bantads.auth.strategy.SagaCommandStrategy;
import com.bantads.shared.dto.OrchestrationCommandDTO;

@Component
public class SagaLogoutStrategy implements SagaCommandStrategy {

    @Autowired
    private JwtService jwtService;

    @Override
    public Object handle(OrchestrationCommandDTO cmd) {
        jwtService.revokeToken(cmd.payload());
        return null;
    }

}
