package com.bantads.gerente.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GerenteCompletoDTO(
  GerenteDTO gerente, 
  List<GerenteClienteDTO> clientes, 
  @JsonProperty("saldo_positivo") BigDecimal saldoPositivo,
  @JsonProperty("saldo_positivo") BigDecimal saldoNegativo
) {}
