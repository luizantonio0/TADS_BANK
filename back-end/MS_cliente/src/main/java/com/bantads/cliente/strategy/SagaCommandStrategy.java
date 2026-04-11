package com.bantads.cliente.strategy;

import com.bantads.cliente.dto.orchestrator.OrchestrationCommandDTO;

public interface SagaCommandStrategy<T> {

    void handle(OrchestrationCommandDTO<T> cmd) throws Exception;

}
