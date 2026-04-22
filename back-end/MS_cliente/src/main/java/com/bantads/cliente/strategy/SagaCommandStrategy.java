package com.bantads.cliente.strategy;

import com.bantads.cliente.dto.orchestrator.OrchestrationCommandDTO;

public interface SagaCommandStrategy<T> {

    Object handle(OrchestrationCommandDTO<T> cmd) throws Exception;

}
