package com.bantads.shared.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.UUID;

@JsonTypeName("OrchestrationCommandResultDTO")
public record OrchestrationCommandResultDTO(UUID idCommand, UUID idOrchestration, String message, boolean ok, String payload) { }
