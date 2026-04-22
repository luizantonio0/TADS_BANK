package com.bantads.orquestrador.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Orchestration implements Serializable {

    private UUID id;
    private boolean failed;
    private Map<String, String> errors;
    private List<Command<?>> commands;
    private Map<String, String> payloads;

    public Orchestration(UUID id, boolean failed, Map<String, String> errors, List<Command<?>> commands, Map<String, String> payloads) {
        this.id = id;
        this.commands = commands;
        this.failed = failed;
        this.errors = errors;
    }

    public Orchestration() {}

    public boolean failed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        if(this.failed) return;
        this.failed = failed;
    }

    public UUID id() {
        return id;
    }

    public List<Command<?>> commands() {
        return commands;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public Map<String, String> getPayloads() {
        return payloads;
    }
}
