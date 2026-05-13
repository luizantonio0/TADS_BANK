package com.bantads.cliente.dto.saga;

import java.util.UUID;

public record GerenteDTO(
        UUID id,
        String cpf,
        String nome,
        String email,
        String tipo,
        Integer totalClientes
) {
        public GerenteDTO {
                cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        }
}