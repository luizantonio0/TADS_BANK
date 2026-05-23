package com.bantads.gerente.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ClienteDTO(
    UUID id,
    String cpf,
    String email,
    String nome,
    String telefone,
    BigDecimal salario,
    BigDecimal saldo,
    BigDecimal limite,
    String endereco,
    String cep,
    String cidade,
    String estado,
    boolean aprovado,
    @JsonProperty("gerente")
    String cpfGerente,
    @JsonProperty("gerente_nome")
    String nomeGerente,
    @JsonProperty("gerente_email")
    String emailGerente,
    String conta,
    String criacao
) {

    public ClienteDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        cep = (cep != null) ? cep.replaceAll("\\D", "") : null;
        telefone = (telefone != null) ? telefone.replaceAll("\\D", "") : null;
    }
}
