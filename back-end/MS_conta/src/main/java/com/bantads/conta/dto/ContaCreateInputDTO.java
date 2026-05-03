package com.bantads.conta.dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.math.BigDecimal;

@JsonTypeName("ContaCreateInputDTO")
public record ContaCreateInputDTO(String cpf, BigDecimal salario, String gerenteCpf) {}
