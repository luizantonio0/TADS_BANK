package com.bantads.conta.dto.orchestrator;

import java.util.UUID;

public record OrchestrationCommandDTO<T> (
        UUID idCommand,
        UUID idOrchestration,
        String serviceName,
        String commandType,
        T dto
) {}
