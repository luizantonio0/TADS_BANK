package com.bantads.conta.dto.orchestrator;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.UUID;

@JsonTypeName("OrchestrationConfirmDTO")
public record OrchestrationConfirmDTO (UUID idOrchestration, String message, boolean ok) { }
