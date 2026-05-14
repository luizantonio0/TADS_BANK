package com.bantads.gerente.dto.request;

import com.bantads.gerente.enums.GerenteTipo;

public record CriaGerenteDTO(
        String nome,
        String email,
        String cpf,
        String senha,
        String telefone,
        GerenteTipo tipo
) {
        public CriaGerenteDTO {
                cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
                telefone = (telefone != null) ? telefone.replaceAll("\\D", "") : null;
        }
}
