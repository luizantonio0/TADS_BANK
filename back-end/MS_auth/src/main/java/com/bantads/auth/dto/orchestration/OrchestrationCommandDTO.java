package com.bantads.auth.dto.orchestration;

import java.util.UUID;

public record OrchestrationCommandDTO<T> (
        UUID idOrchestration,
        UUID idCommand,
        String serviceName,
        String commandType,
        T dto
) {}
