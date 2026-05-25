package com.bantads.auth.strategy;

import com.bantads.auth.strategy.strategies.SagaAuthStrategy;
import com.bantads.auth.strategy.strategies.SagaCreateCredentialsStrategy;
import com.bantads.auth.strategy.strategies.SagaLogoutStrategy;

import com.bantads.auth.strategy.strategies.SagaUpdateCredentialsStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired private SagaCreateCredentialsStrategy createCredentialsStrategy;
    @Autowired private SagaUpdateCredentialsStrategy updateCredentialsStrategy;
    @Autowired private SagaAuthStrategy authStrategy;
    @Autowired private SagaLogoutStrategy logoutStrategy;

    public SagaCommandStrategy newCommand(String commandType) {
        return switch(commandType) {
            case "CreateCredentials" -> createCredentialsStrategy;
            case "Login" -> authStrategy;
            case "Logout" -> logoutStrategy;
            case "UpdateCredentials" -> updateCredentialsStrategy;
            default -> throw new IllegalArgumentException("Unexpected value: " + commandType);
        };
    }

}
