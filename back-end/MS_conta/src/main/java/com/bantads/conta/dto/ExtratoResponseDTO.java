package com.bantads.conta.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExtratoResponseDTO(
    String conta,
    BigDecimal saldo,
    List<MovimentacaoDTO> movimentacoes,
    @JsonProperty("saldos_consolidados") Map<String, BigDecimal> saldosConsolidadosPorDia
) {}
