package com.bantads.conta.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_conta")
@Data
@Audited
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(unique = true, nullable = false, length = 10)
    private String conta;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(nullable = false)
    private BigDecimal limite;

    @Column(nullable = false)
    private LocalDateTime criacao;

    public Conta(LocalDateTime criacao, BigDecimal limite, BigDecimal saldo, String conta, String cpf) {
        this.criacao = criacao;
        this.limite = limite;
        this.saldo = saldo;
        this.conta = conta;
        this.cpf = cpf.replaceAll("[^0-9]", "");;
    }

    public Conta() {}

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
}
