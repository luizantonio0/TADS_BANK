package com.bantads.gerente.enums;

public enum GerenteTipo {
    ADMINISTRADOR("ADMINISTRADOR"),
    GERENTE("GERENTE");

    private String nome;

    GerenteTipo(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
