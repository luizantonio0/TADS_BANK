package com.bantads.cliente.saga.orchestrator;

import com.bantads.cliente.saga.enums.Service;

import java.util.concurrent.ThreadLocalRandom;

public class OrchestratorCommand<T> {

    private long id;
    private long orchestrationId;
    private boolean ok;
    private T dto;
    private Service target;

    public OrchestratorCommand(Service target, long orchestrationId, T dto) {
        this.id = ThreadLocalRandom.current().nextLong();
        this.dto = dto;
        this.target = target;
        this.orchestrationId = orchestrationId;
    }

    public long getId() {
        return id;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean b) {
        this.ok = b;
    }

    public long getOrchestrationId() {
        return orchestrationId;
    }

    public Service getTarget() {
        return target;
    }

    public T getDto() {
        return dto;
    }
}
