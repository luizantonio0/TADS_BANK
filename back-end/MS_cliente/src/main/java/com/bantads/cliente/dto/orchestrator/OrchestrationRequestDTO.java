package com.bantads.cliente.dto.orchestrator;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.UUID;

@JsonTypeName("OrchestrationRequestDTO")
public record OrchestrationRequestDTO(
        UUID uuid,
        List<OrchestrationCommandDTO<?>> commands
) { }
