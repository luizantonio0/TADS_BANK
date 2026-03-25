package com.bantads.orquestrador.dto;

import com.bantads.orquestrador.model.Command;

import java.util.UUID;

public record OrchestrationCommandDTO<T> (
        String serviceName,
        String commandType,
        T dto
) {
    public Command<T> toCommand() {
        return new Command<>(UUID.randomUUID(), this.commandType(), this.serviceName(), this.dto());
    }
}
