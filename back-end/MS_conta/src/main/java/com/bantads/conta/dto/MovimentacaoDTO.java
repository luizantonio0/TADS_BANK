package com.bantads.conta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MovimentacaoDTO(
    @JsonProperty("data_hora") LocalDateTime dataHora,
    String tipo,
    @JsonProperty("conta_origem") String contaOrigem,
    @JsonProperty("conta_destino") String contaDestino,
    BigDecimal valor
) {}
