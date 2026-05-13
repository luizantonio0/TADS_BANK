package com.bantads.cliente.dto.http;


import java.math.BigDecimal;

public record AlterarDadosClienteDTO (
    String nome,
    String email,
    BigDecimal salario,
    String endereco,
    String CEP,
    String cidade,
    String estado
){
    public AlterarDadosClienteDTO {
        CEP = (CEP != null) ? CEP.replaceAll("\\D", "") : null;
    }
}
