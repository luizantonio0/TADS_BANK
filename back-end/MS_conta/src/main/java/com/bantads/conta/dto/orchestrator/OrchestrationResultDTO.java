package com.bantads.conta.dto.orchestrator;

import java.util.UUID;

public record OrchestrationResultDTO(UUID idCommand, UUID idOrchestration, String message, boolean ok) { }
