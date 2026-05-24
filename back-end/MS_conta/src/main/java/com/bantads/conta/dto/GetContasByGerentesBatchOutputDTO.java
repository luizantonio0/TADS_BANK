package com.bantads.conta.dto;

import java.math.BigDecimal;
import java.util.Map;

public record GetContasByGerentesBatchOutputDTO(Map<String, ContaDTO> contas, BigDecimal saldoPositivo, BigDecimal saldoNegativo) {
}
