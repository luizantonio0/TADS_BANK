package com.bantads.auth.dto.orchestration;

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
