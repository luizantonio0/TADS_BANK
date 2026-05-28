package com.bantads.gerente.dto.response;

import com.bantads.gerente.model.Gerente;

public record GerenteAtualizadoDTO(
    String cpf,
    String nome,
    String email,
    String tipo
) {
    public GerenteAtualizadoDTO(Gerente gerente) {
        this(
            gerente.getCpf(),
            gerente.getNome(),
            gerente.getEmail(),
            gerente.getTipo()
        );
    }
}
