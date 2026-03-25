package com.bantads.cliente.saga.dto;

public record OrchestrationResultDTO(long idOrchestration, long idCommand, boolean ok) {}