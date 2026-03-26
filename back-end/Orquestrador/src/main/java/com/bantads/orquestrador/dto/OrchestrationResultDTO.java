package com.bantads.orquestrador.dto;

import java.util.UUID;

public record OrchestrationResultDTO (UUID idCommand, UUID idOrchestration, boolean ok) { }
