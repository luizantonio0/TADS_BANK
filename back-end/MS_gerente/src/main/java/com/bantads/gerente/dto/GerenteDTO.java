package com.bantads.gerente.dto;

import com.bantads.gerente.model.Gerente;
import java.util.UUID;

public record GerenteDTO(
        UUID id,
        String cpf,
        String nome,
        String email,
        String tipo,
        Integer totalClientes
) {
    public static GerenteDTO from(Gerente gerente) {
        if (gerente == null) return null;
        return new GerenteDTO(
                gerente.getId(),
                gerente.getCpf(),
                gerente.getNome(),
                gerente.getEmail(),
                gerente.getTipo(),
                gerente.getTotalClientes()
        );
    }
}