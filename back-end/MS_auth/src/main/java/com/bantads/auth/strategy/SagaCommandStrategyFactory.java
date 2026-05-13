package com.bantads.auth.strategy;

import com.bantads.auth.strategy.strategies.SagaAuthStrategy;
import com.bantads.auth.strategy.strategies.SagaCreateCredentialsStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    @Autowired private SagaCreateCredentialsStrategy createCredentialsStrategy;
    @Autowired private SagaAuthStrategy authStrategy;

    public SagaCommandStrategy newCommand(String commandType) {
        return switch(commandType) {
            case "CreateCredentials" -> createCredentialsStrategy;
            case "Login" -> authStrategy;
            default -> throw new IllegalStateException("Unexpected value: " + commandType);
        };
    }

}
