package com.bantads.auth.dto.orchestration;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.UUID;

@JsonTypeName("OrchestrationCommandResultDTO")
public record OrchestrationCommandResultDTO(UUID idCommand, UUID idOrchestration, String message, boolean ok, String payload) { }
