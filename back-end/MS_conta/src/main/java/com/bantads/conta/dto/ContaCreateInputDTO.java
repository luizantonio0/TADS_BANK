package com.bantads.conta.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.math.BigDecimal;
import java.util.UUID;

@JsonTypeName("ContaCreateInputDTO")
public record ContaCreateInputDTO(String cpf, BigDecimal salario) {}
