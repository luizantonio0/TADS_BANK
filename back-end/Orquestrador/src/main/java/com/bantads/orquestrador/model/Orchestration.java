package com.bantads.orquestrador.model;

import com.bantads.shared.dto.OrchestrationCommandDTO;

import java.io.Serializable;
import java.util.*;

public class Orchestration implements Serializable {
    private UUID id;
    private int latch;
    private boolean failed;
    private boolean autoConfirm;
    private boolean finished;
    private Map<String, String> errors = new HashMap<>();
    private List<OrchestrationCommandDTO> commands = new ArrayList<>();
    private Map<String, String> payloads = new HashMap<>();

    public Orchestration() {}

    public Orchestration(UUID id, int latch, boolean failed, boolean finished, boolean autoConfirm, Map<String, String> errors, List<OrchestrationCommandDTO> commands, Map<String, String> payloads) {
        this.id = id;
        this.latch = latch;
        this.commands = commands;
        this.failed = failed;
        this.autoConfirm = autoConfirm;
        this.errors = errors;
        this.payloads = payloads;
        this.finished = finished;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public boolean isFailed() { return failed; }
    public void setFailed(boolean failed) {
        if(this.failed) return;
        this.failed = failed;
    }

    public boolean isAutoConfirm() {
        return autoConfirm;
    }

    public void setAutoConfirm(boolean autoConfirm) {
        this.autoConfirm = autoConfirm;
    }

    public List<OrchestrationCommandDTO> getCommands() { return commands; }
    public void setCommands(List<OrchestrationCommandDTO> commands) { this.commands = commands; }

    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }

    public Map<String, String> getPayloads() { return payloads; }
    public void setPayloads(Map<String, String> payloads) { this.payloads = payloads; }

    public int getLatch() {
        return latch;
    }

    public void setLatch(int latch) {
        this.latch = latch;
    }

    public boolean decrementLatchAndTest() {
        this.latch = Math.max(0, this.latch-1);
        return this.latch == 0;
    }
}