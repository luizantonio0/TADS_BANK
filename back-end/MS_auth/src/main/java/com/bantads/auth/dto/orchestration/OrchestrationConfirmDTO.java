package com.bantads.auth.dto.orchestration;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.UUID;

@JsonTypeName("OrchestrationConfirmDTO")
public record OrchestrationConfirmDTO (UUID idOrchestration, String message, boolean ok) { }
