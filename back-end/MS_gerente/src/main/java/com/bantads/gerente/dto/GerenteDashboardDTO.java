package com.bantads.gerente.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GerenteDashboardDTO(
  GerenteDTO gerente, 
  List<GerenteClienteDashboardDTO> clientes, 
  @JsonProperty("saldo_positivo") BigDecimal saldoPositivo,
  @JsonProperty("saldo_negativo") BigDecimal saldoNegativo
) {}
