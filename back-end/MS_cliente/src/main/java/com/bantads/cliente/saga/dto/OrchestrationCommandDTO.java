package com.bantads.cliente.saga.dto;

public record OrchestrationCommandDTO<T> (long idOrchestration, String targetService, String orchestrationType, T dto) {
}
