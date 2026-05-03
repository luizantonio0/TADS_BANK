package com.bantads.conta.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ExtratoResponseDTO(
    List<MovimentacaoDTO> movimentacoes,
    Map<String, BigDecimal> saldosConsolidadosPorDia
) {}
