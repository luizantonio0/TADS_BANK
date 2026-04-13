package com.bantads.cliente.dto.orchestrator;

import java.util.UUID;

public record OrchestrationCommandDTO<T> (
        UUID idOrchestration,
        UUID idCommand,
        String serviceName,
        String commandType,
        T dto
) {}
