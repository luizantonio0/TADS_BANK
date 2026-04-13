package com.bantads.cliente.dto.orchestrator;

import java.util.UUID;

public record OrchestrationConfirmDTO (UUID idOrchestration, String message, boolean ok) { }
