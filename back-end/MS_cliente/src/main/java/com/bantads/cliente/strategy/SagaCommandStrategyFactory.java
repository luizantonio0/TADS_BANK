package com.bantads.cliente.strategy;

import org.springframework.stereotype.Component;

@Component
public class SagaCommandStrategyFactory {

    public SagaCommandStrategy newCommand(String commandType) {
        throw new IllegalStateException("Unexpected value: " + commandType);
    }

}
