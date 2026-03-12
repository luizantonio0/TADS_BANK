package com.bantads.cliente.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record AprovarClienteDTO(
        String cpf,
        String nome,
        String email,
        BigDecimal salario,
        String endereco,
        String cidade,
        String estado
) implements Serializable {
}
