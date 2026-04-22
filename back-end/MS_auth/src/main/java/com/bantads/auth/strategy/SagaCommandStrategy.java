package com.bantads.auth.strategy;

import com.bantads.auth.dto.orchestration.OrchestrationCommandDTO;

public interface SagaCommandStrategy<T> {

    Object handle(OrchestrationCommandDTO<T> cmd);

}
