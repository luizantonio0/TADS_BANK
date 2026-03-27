package com.bantads.orquestrador.model;

import java.util.List;
import java.util.UUID;

public class Orchestration {

    private UUID id;
    private boolean failed;
    private List<String> errors;
    private List<Command<?>> commands;

    public Orchestration(UUID id, boolean failed, List<String> errors, List<Command<?>> commands) {
        this.id = id;
        this.commands = commands;
        this.failed = failed;
        this.errors = errors;
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

    public List<String> getErrors() {
        return errors;
    }
}
