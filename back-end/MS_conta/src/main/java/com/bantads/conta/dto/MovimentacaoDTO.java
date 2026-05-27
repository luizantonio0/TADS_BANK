package com.bantads.conta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MovimentacaoDTO(
    @JsonProperty("data") LocalDateTime dataHora,
    String tipo,
    @JsonProperty("origem") String contaOrigem,
    @JsonProperty("destino") String contaDestino,
    BigDecimal valor
) {}
