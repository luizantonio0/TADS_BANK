package com.bantads.cliente.dto;

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
}
