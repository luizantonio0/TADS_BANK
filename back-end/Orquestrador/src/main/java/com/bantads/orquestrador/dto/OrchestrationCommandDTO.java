package com.bantads.orquestrador.dto;

import com.bantads.orquestrador.model.Command;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.UUID;

@JsonTypeName("OrchestrationCommandDTO")
public record OrchestrationCommandDTO<T> (
        UUID idOrchestration,
        UUID idCommand,
        String serviceName,
        String commandType,
        T dto
) {

    public Command<T> toCommand() {
        return new Command<>(idCommand, commandType, serviceName, dto);
    }

}
