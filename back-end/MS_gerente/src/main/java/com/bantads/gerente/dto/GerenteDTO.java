package com.bantads.gerente.dto;

import com.bantads.gerente.model.Gerente;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GerenteDTO(
        String cpf,
        String nome,
        String email,
        String tipo,
        Integer totalClientes) {

    public GerenteDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }

    public static GerenteDTO from(Gerente gerente, boolean includeTotalClientes) {
        if (gerente == null)
            return null;
        return new GerenteDTO(
                gerente.getCpf(),
                gerente.getNome(),
                gerente.getEmail(),
                gerente.getTipo(),
                includeTotalClientes ? gerente.getTotalClientes() : null);
    }
}