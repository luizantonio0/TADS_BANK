package com.bantads.cliente.dto.orchestrator;

import java.util.List;
import java.util.UUID;

public record OrchestrationConfirmDTO (UUID idOrchestration, List<String> errors, boolean ok) { }
