package com.bantads.conta.strategy;


import com.bantads.conta.dto.orchestrator.OrchestrationCommandDTO;

public interface SagaCommandStrategy<T> {

    void handle(OrchestrationCommandDTO<T> cmd) throws Exception;

}
