package com.bantads.orquestrador.dto;

import java.util.UUID;

public record OrchestrationConfirmDTO (UUID idOrchestration, boolean ok) { }
