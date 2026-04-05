package com.bantads.orquestrador.dto;

import java.util.List;
import java.util.UUID;

public record OrchestrationConfirmDTO (UUID idOrchestration, List<String> errors, boolean ok) { }
