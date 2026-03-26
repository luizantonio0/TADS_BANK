package com.bantads.orquestrador.model;

import java.util.List;
import java.util.UUID;

public class Orchestration {

    private UUID id;
    private boolean failed;
    private List<Command<?>> commands;

    public Orchestration(UUID id, List<Command<?>> commands) {
        this.id = id;
        this.commands = commands;
        this.failed = false;
    }

    public boolean failed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public UUID id() {
        return id;
    }

    public List<Command<?>> commands() {
        return commands;
    }
}
