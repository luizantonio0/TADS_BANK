package com.bantads.conta.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
@Table(name = "tb_conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(unique = true, nullable = false)
    private String conta;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(nullable = false)
    private BigDecimal limite;

    @Column(nullable = false)
    private Date criacao;

    public Conta(Date criacao, BigDecimal limite, BigDecimal saldo, String conta, String cpf) {
        this.criacao = criacao;
        this.limite = limite;
        this.saldo = saldo;
        this.conta = conta;
        this.cpf = cpf;
    }

    public UUID getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public String getConta() {
        return conta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public BigDecimal getLimite() {
        return limite;
    }

    public Date getCriacao() {
        return criacao;
    }
}
