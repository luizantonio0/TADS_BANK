package com.bantads.cliente.dto;

import com.bantads.cliente.model.Cliente;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public record ClienteDTO(
    UUID id,
    String cpf,
    String email,
    String nome,
    String telefone,
    BigDecimal salario,
    String endereco,
    String cep,
    String cidade,
    String estado,
    boolean aprovado,
    @JsonProperty("cpf_gerente")
    String cpfGerente,
    String criacao
) {

    public ClienteDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        cep = (cep != null) ? cep.replaceAll("\\D", "") : null;
        telefone = (telefone != null) ? telefone.replaceAll("\\D", "") : null;
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
            c.getEndereco(),
            c.getCep(),
            c.getCidade(),
            c.getEstado() != null ? c.getEstado().name() : null,
            c.isAprovado(),
            c.getCpfGerente(),
            c.getCriacao().toString()
        );
    }

}
