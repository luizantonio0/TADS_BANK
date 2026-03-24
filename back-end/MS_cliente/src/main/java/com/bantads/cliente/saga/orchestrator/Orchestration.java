package com.bantads.cliente.saga.orchestrator;

import com.bantads.cliente.saga.enums.OrchestrationType;

import java.util.concurrent.ThreadLocalRandom;

public class Orchestration {

    private long id;
    private OrchestrationType type;

    public Orchestration(long idOrchestration) {
        this.id = ThreadLocalRandom.current().nextLong();
    }

    public void setOk(boolean ok) {
        this.okResult = ok;
    }

    public <T> void send(OrchestratorCommand<T> cmd) {

    }


}
