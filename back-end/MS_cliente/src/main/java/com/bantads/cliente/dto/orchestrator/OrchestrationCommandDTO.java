package com.bantads.cliente.dto.orchestrator;

import java.util.UUID;

public record OrchestrationCommandDTO<T> (
        UUID idCommand,
        String serviceName,
        String commandType,
        T dto
) {}
