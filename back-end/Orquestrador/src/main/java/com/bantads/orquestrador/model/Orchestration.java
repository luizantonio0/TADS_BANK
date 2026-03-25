package com.bantads.orquestrador.model;

import java.util.List;
import java.util.UUID;

public class Orchestration {

    private UUID idOrchestration;
    private List<Command<?>> commands;

    public Orchestration(UUID idOrchestration, List<Command<?>> commands) {
        this.idOrchestration = idOrchestration;
        this.commands = commands;
    }

    public UUID getIdOrchestration() {
        return idOrchestration;
    }

    public List<Command<?>> getCommands() {
        return commands;
    }
}
