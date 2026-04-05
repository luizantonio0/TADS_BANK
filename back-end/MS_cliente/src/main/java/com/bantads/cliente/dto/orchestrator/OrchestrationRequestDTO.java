package com.bantads.cliente.dto.orchestrator;

import java.util.List;
import java.util.UUID;

public record OrchestrationRequestDTO(
        UUID uuid,
        List<OrchestrationCommandDTO<?>> commands
) { }
