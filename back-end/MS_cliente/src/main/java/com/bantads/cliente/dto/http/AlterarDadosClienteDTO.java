package com.bantads.cliente.dto.http;


import java.math.BigDecimal;

public record AlterarDadosClienteDTO (
    String nome,
    String email,
    BigDecimal salario,
    String endereco,
    String cep,
    String cidade,
    String estado,
    String telefone
){
    public AlterarDadosClienteDTO {
        cep = (cep != null) ? cep.replaceAll("\\D", "") : null;
        telefone = (telefone != null) ? telefone.replaceAll("\\D", "") : null;
    }
}
