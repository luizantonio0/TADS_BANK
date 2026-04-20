package com.bantads.conta.dto.orchestrator;

import java.util.UUID;

public record OrchestrationResultDTO<T>(UUID idCommand, UUID idOrchestration, String message, boolean ok, T data) { }
