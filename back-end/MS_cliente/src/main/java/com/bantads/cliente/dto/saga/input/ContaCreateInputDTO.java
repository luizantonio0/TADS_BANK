package com.bantads.cliente.dto.saga.input;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.math.BigDecimal;

@JsonTypeName("ContaCreateInputDTO")
public record ContaCreateInputDTO(String cpf, BigDecimal salario) {}
