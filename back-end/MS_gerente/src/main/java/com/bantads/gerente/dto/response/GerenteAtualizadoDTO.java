package com.bantads.gerente.dto.response;

import com.bantads.gerente.enums.GerenteTipo;
import com.bantads.gerente.model.Gerente;

public record GerenteAtualizadoDTO(
        String nome,
        String email,
        GerenteTipo tipo
) {
    public GerenteAtualizadoDTO(Gerente gerente) {
        this(
                gerente.getNome(),
                gerente.getEmail(),
                GerenteTipo.valueOf(gerente.getTipo())
        );
    }
}
