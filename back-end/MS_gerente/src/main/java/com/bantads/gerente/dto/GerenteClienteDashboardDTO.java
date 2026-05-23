package com.bantads.gerente.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GerenteClienteDashboardDTO(
    String cliente,
    String numero,
    BigDecimal saldo,
    BigDecimal limite,
    @JsonProperty("gerente")
    String cpfGerente,
    String criacao
) {

    public GerenteClienteDashboardDTO {
        cliente = (cliente != null) ? cliente.replaceAll("\\D", "") : null;
    }

}
