package com.bantads.cliente.saga.orchestrator;

import com.bantads.cliente.saga.enums.OrchestrationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class Orchestration {

    private long id;
    private CountDownLatch latch;
    private OrchestrationType type;
    private Map<Long, OrchestratorCommand<?>> commands = new HashMap<>();

    public Orchestration(long idOrchestration, List<OrchestratorCommand<?>> commands) {
        this.id = ThreadLocalRandom.current().nextLong();
        commands.forEach(x -> this.commands.put(x.getId(), x));
        this.latch = new CountDownLatch(commands.size());
    }

    public long getId() {
        return id;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public OrchestrationType getType() {
        return type;
    }

    public Map<Long, OrchestratorCommand<?>> getCommands() {
        return commands;
    }
}
