package com.bantads.cliente.dto.conta;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.math.BigDecimal;
import java.util.UUID;

@JsonTypeName("ContaCreateInputDTO")
public record ContaCreateOutputDTO(UUID uuid, String numero, BigDecimal saldo, BigDecimal limite) {
}
