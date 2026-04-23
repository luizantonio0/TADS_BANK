package com.bantads.cliente.strategy;

import com.bantads.shared.dto.OrchestrationCommandDTO;

public interface SagaCommandStrategy {

    Object handle(OrchestrationCommandDTO cmd) throws Exception;

}
