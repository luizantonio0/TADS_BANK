package com.bantads.conta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MovimentacaoResultDTO(
    String conta,
    LocalDateTime data,
    BigDecimal saldo,
    BigDecimal valor,
    String destino
) {}
