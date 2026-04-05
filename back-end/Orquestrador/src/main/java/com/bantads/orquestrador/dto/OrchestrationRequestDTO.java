package com.bantads.orquestrador.dto;

import java.util.List;
import java.util.UUID;

public record OrchestrationRequestDTO(
        UUID uuid,
        List<OrchestrationCommandDTO<?>> commands
) { }
