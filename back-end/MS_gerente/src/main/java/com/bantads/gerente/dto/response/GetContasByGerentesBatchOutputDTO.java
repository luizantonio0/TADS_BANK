package com.bantads.gerente.dto.response;

import java.math.BigDecimal;
import java.util.Map;

import com.bantads.gerente.dto.ContaDTO;

public record GetContasByGerentesBatchOutputDTO(Map<String, ContaDTO> contas, BigDecimal saldoPositivo, BigDecimal saldoNegativo) {
}
