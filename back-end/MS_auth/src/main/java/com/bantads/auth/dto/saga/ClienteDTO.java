package com.bantads.auth.dto.saga;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClienteDTO(
    UUID id,
    String cpf,
    String email,
    String nome,
    String telefone,
    BigDecimal salario,
    String endereco,
    String cep,
    String cidade,
    String estado,
    boolean aprovado,
    @JsonProperty("cpf_gerente")
    String cpfGerente,
    String criacao
) {
    public ClienteDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        cep = (cep != null) ? cep.replaceAll("\\D", "") : null;
        telefone = (telefone != null) ? telefone.replaceAll("\\D", "") : null;
    }
}
