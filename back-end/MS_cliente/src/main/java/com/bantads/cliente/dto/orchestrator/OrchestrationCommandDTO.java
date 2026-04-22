package com.bantads.cliente.dto.orchestrator;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.UUID;

@JsonTypeName("OrchestrationCommandDTO")
public record OrchestrationCommandDTO<T> (
        UUID idOrchestration,
        UUID idCommand,
        String serviceName,
        String commandType,
        T dto
) {}
