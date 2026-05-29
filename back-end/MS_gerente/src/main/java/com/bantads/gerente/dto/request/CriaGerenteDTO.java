package com.bantads.gerente.dto.request;

import java.math.BigDecimal;
import java.util.Map;

import com.bantads.gerente.enums.GerenteTipo;
import com.fasterxml.jackson.annotation.JsonProperty;

public record CriaGerenteDTO(
        String nome,
        String email,
        String cpf,
        String senha,
        GerenteTipo tipo,
        @JsonProperty("saldos_negativos") Map<String, BigDecimal> saldosNegativos
) {
        public CriaGerenteDTO {
                cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        }
}
