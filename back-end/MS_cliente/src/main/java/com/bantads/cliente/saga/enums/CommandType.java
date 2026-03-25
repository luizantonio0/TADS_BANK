package com.bantads.cliente.saga.enums;

public enum CommandType {
    ;

    private String key;

    CommandType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
