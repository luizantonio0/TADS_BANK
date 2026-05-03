package com.bantads.gerente.strategy;

import com.bantads.shared.dto.*;

public interface SagaCommandStrategy {

    Object handle(OrchestrationCommandDTO cmd) throws Exception;

}
