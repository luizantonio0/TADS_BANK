package com.bantads.shared.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;
import java.util.UUID;

@JsonTypeName("OrchestrationRequestDTO")
public record OrchestrationRequestDTO(
        UUID uuid,
        boolean autoConfirm,
        List<OrchestrationCommandDTO> commands
)
{ }
