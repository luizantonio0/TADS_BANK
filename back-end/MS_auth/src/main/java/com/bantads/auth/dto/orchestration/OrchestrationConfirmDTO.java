package com.bantads.auth.dto.orchestration;

import java.util.UUID;

public record OrchestrationConfirmDTO (UUID idOrchestration, boolean ok) { }
