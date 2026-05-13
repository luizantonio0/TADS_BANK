package com.bantads.cliente.dto.http;

import java.math.BigDecimal;

public record TodosClientesResponseDTO(
        String cpf,
        String nome,
        String email,
        String telefone,
        String endereco,
        String cidade,
        String estado,
        String conta,
        BigDecimal saldo,
        BigDecimal limite
) {
        public TodosClientesResponseDTO {
                cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
                telefone = (telefone != null) ? telefone.replaceAll("\\D", "") : null;
        }
}
