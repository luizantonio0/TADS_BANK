package com.bantads.shared.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;
import java.util.UUID;

@JsonTypeName("OrchestrationRequestResultDTO")
public record OrchestrationRequestResultDTO(
        UUID idOrchestration,
        boolean failed,
        Map<String, String> payloads,
        Map<String, String> errors
) {
}
