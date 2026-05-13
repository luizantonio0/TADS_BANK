package com.bantads.cliente.dto;

import com.bantads.cliente.model.Cliente;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    UUID idGerente,
    LocalDateTime criacao
) {

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
            c.getIdGerente(),
            c.getCriacao()
        );
    }

}
