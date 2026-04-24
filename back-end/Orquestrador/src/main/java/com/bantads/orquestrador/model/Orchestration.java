package com.bantads.orquestrador.model;

import com.bantads.shared.dto.OrchestrationCommandDTO;

import java.io.Serializable;
import java.util.*;

public class Orchestration implements Serializable {
    private UUID id;
    private int latch;
    private boolean failed;
    private Map<String, String> errors = new HashMap<>();
    private List<OrchestrationCommandDTO> commands = new ArrayList<>();
    private Map<String, String> payloads = new HashMap<>();

    public Orchestration() {}

    public Orchestration(UUID id, int latch, boolean failed, Map<String, String> errors, List<OrchestrationCommandDTO> commands, Map<String, String> payloads) {
        this.id = id;
        this.latch = latch;
        this.commands = commands;
        this.failed = failed;
        this.errors = errors;
        this.payloads = payloads;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public boolean isFailed() { return failed; }
    public void setFailed(boolean failed) {
        if(this.failed) return;
        this.failed = failed;
    }

    public List<OrchestrationCommandDTO> getCommands() { return commands; }
    public void setCommands(List<OrchestrationCommandDTO> commands) { this.commands = commands; }

    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }

    public Map<String, String> getPayloads() { return payloads; }
    public void setPayloads(Map<String, String> payloads) { this.payloads = payloads; }

    public boolean decrementLatchAndTest() {
        this.latch = Math.max(0, this.latch-1);
        return this.latch == 0;
    }
}