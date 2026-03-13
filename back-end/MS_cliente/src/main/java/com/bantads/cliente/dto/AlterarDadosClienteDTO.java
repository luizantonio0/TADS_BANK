package com.bantads.cliente.dto;

import com.bantads.cliente.enums.UF;

import java.math.BigDecimal;

public record AlterarDadosClienteDTO (
        String email,
        String senha,
        String nome,
        String telefone,
        BigDecimal salario,
        String endereco,
        String CEP,
        String cidade,
        UF estado
){
}
