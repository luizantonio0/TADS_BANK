package com.bantads.auth.dto.orchestration;

import java.util.List;
import java.util.UUID;

public record OrchestrationRequestDTO(
        UUID uuid,
        List<OrchestrationCommandDTO<?>> commands
) { }
