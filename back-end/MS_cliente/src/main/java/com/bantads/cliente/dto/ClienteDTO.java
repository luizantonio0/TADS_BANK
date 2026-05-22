package com.bantads.cliente.dto;

import com.bantads.cliente.model.Cliente;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClienteDTO(
    UUID id,
    String cpf,
    String email,
    String nome,
    String telefone,
    BigDecimal salario,
    BigDecimal saldo,
    BigDecimal limite,
    String endereco,
    String cep,
    String cidade,
    String estado,
    boolean aprovado,
    @JsonProperty("gerente")
    String cpfGerente,
    @JsonProperty("gerente_nome")
    String nomeGerente,
    @JsonProperty("gerente_email")
    String emailGerente,
    String conta,
    String criacao
) {

    public ClienteDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        cep = (cep != null) ? cep.replaceAll("\\D", "") : null;
        telefone = (telefone != null) ? telefone.replaceAll("\\D", "") : null;
    }

    public ClienteDTO(String cpf, String nome, String telefone, String email, BigDecimal salario, BigDecimal saldo, BigDecimal limite, String endereco, String cidade, String estado, Boolean aprovado, String cpfGerente, String nomeGerente, String emailGerente, String conta, String criacao) {
        this(null, cpf, email, nome, telefone, salario, saldo, limite, endereco, null, cidade, estado, aprovado, cpfGerente, nomeGerente, emailGerente, conta, criacao);
    }
    
    public static ClienteDTO from(Cliente c) {
        if (c == null) return null;
        return new ClienteDTO(
            c.getId(),
            c.getCpf(),
            c.getEmail(),
            c.getNome(),
            c.getTelefone(),
            c.getSalario(),
            null,
            null,
            c.getEndereco(),
            c.getCep(),
            c.getCidade(),
            c.getEstado() != null ? c.getEstado().name() : null,
            c.isAprovado(),
            c.getCpfGerente(),
            null,
            null,
            null,
            c.getCriacao().toString()
        );
    }

}
