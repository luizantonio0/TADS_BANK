package com.bantads.cliente.saga.enums;

public enum Service {
    MS_CONTA("ms-conta"),
    MS_AUTH("ms-auth"),
    MS_GERENTE("ms-gerente"),
    MS_CLIENTE("ms-cliente");

    private String key;

    Service(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
