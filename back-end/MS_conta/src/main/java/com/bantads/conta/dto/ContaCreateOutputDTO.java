package com.bantads.conta.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.math.BigDecimal;

@JsonTypeName("ContaCreateInputDTO")
public record ContaCreateOutputDTO(String cpf, String numero, BigDecimal saldo, BigDecimal limite) {
}
