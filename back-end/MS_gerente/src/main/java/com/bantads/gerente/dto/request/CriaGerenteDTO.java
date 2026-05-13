package com.bantads.gerente.dto.request;

import com.bantads.gerente.enums.GerenteTipo;

public record CriaGerenteDTO(
        String nome,
        String email,
        String cpf,
        String senha,
        GerenteTipo tipo
) {
        public CriaGerenteDTO {
                cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
        }
}
