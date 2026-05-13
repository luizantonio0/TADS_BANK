package com.bantads.cliente.dto.http;

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
) {
    public ClienteRequestDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        CEP = (CEP != null) ? CEP.replaceAll("\\D", "") : null;
        telefone = (telefone != null) ? telefone.replaceAll("\\D", "") : null;
    }
}
