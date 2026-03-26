package com.bantads.auth.dto.orchestration;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrchestrationResultDTO (
        UUID idCommand,
        UUID idOrchestration,
        String message,
        boolean ok
) { }
