package com.bantads.auth.strategy;

import com.bantads.auth.dto.orchestration.OrchestrationCommandDTO;
import com.bantads.auth.dto.orchestration.OrchestrationResultDTO;

public interface SagaCommandStrategy<T> {

    void handle(OrchestrationCommandDTO<T> cmd);

}
