package com.bantads.cliente.saga.enums;

public enum OrchestrationType {
    CREATE_ACCOUNT("create-account");

    private String key;

    OrchestrationType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
