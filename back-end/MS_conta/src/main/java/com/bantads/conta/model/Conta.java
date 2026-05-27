package com.bantads.conta.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_conta", schema = "public")
@Data
@Audited
public class Conta {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(name = "cpfGerente", unique = true, nullable = false, length = 11)
    private String cpfGerente;

    @Column(unique = true, nullable = false, length = 10)
    private String conta;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(nullable = false)
    private BigDecimal limite;

    @Column(nullable = false)
    private LocalDateTime criacao;

    public Conta(LocalDateTime criacao, BigDecimal limite, BigDecimal saldo, String conta, String cpf, String cpfGerente) {
        this.criacao = criacao;
        this.limite = limite;
        this.saldo = saldo;
        this.conta = conta;
        this.cpf = cpf.replaceAll("[^0-9]", "");
        this.cpfGerente = cpfGerente.replaceAll("[^0-9]", "");
    }

    public Conta() {}

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
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

    public LocalDateTime getCriacao() {
        return criacao;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public void setLimite(BigDecimal limite) {
        this.limite = limite;
    }

    public void setCriacao(LocalDateTime criacao) {
        this.criacao = criacao;
    }

    public String getCpfGerente() {
        return cpfGerente;
    }

    public void setCpfGerente(String cpfGerente) {
        this.cpfGerente = cpfGerente;
    }

}
