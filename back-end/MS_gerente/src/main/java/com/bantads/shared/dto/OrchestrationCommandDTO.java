package com.bantads.shared.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.UUID;

@JsonTypeName("OrchestrationCommandDTO")
public record OrchestrationCommandDTO (
        UUID idOrchestration,
        UUID idCommand,
        String serviceName,
        String commandType,
        String payload
) {
}
