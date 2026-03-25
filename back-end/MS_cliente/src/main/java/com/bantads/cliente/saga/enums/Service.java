package com.bantads.cliente.saga.enums;

public enum Service {
    MS_CONTA("ms-conta", "ms-conta.commands"),
    MS_AUTH("ms-auth", "ms-auth.commands"),
    MS_GERENTE("ms-gerente", "ms-gerente.commands"),
    MS_CLIENTE("ms-cliente", "ms-cliente.commands");

    private String key;
    private String commandQueue;

    Service(String key, String commandQueue) {
        this.key = key;
        this.commandQueue = commandQueue;
    }

    public String getCommandQueue() {
        return commandQueue;
    }

    public String getKey() {
        return key;
    }
}
