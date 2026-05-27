package com.bantads.conta.model;

public enum TipoMovimentacao {
    DEPOSITO("depósito"),
    SAQUE("saque"),
    TRANSFERENCIA("transferência");

    private String nome;

    TipoMovimentacao(String nome) {
        this.nome = nome;
    }

    public String nome() {
        return nome;
    }
}
