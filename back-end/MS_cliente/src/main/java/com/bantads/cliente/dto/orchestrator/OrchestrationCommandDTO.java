package com.bantads.cliente.dto.orchestrator;

import com.bantads.orquestrador.model.Command;

import java.util.UUID;

public record OrchestrationCommandDTO<T> (
        UUID idCommand,
        String serviceName,
        String commandType,
        T dto
) {
    public Command<T> toCommand() {
        return new Command<>(idCommand, this.commandType(), this.serviceName(), this.dto());
    }
}
