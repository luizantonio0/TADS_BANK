package com.bantads.cliente.saga.orchestrator;

import com.bantads.cliente.saga.enums.Service;

import java.util.concurrent.ThreadLocalRandom;

public class OrchestratorCommand<T> {

    private final long id;
    private long idOrchestration;
    private final T dto;
    private final Service target;

    public OrchestratorCommand(Service target, long idOrchestration, T dto) {
        this.id = ThreadLocalRandom.current().nextLong();
        this.dto = dto;
        this.target = target;
        this.idOrchestration = idOrchestration;
    }

    public long getId() {
        return id;
    }

    public long getOrchestrationId() {
        return idOrchestration;
    }

    public Service getTarget() {
        return target;
    }

    public T getDto() {
        return dto;
    }
}
