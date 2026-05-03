package com.bantads.cliente.dto;

import java.math.BigDecimal;

public record ClienteCreateResponseDTO(
        String cpf,
        String email,
        String nome,
        String telefone,
        BigDecimal salario,
        String endereco,
        String CEP,
        String cidade,
        String estado
) { }
