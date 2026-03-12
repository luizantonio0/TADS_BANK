package com.bantads.cliente.dto;

import com.bantads.cliente.enums.UF;

import java.math.BigDecimal;

public record ClienteRequestDTO
(
    String cpf,
    String email,
    String nome,
    String telefone,
    BigDecimal salario,
    String endereco,
    String CEP,
    String cidade,
    UF estado
)
{
}
