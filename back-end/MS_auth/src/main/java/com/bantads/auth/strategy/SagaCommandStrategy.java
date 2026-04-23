package com.bantads.auth.strategy;

import com.bantads.shared.dto.*;

public interface SagaCommandStrategy {

    Object handle(OrchestrationCommandDTO cmd);

}
