package com.bantads.cliente.strategy;

import com.bantads.auth.dto.orchestration.OrchestrationCommandDTO;

public interface SagaCommandStrategy<T> {

    void handle(OrchestrationCommandDTO<T> cmd);

}
