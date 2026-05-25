package com.bantads.gerente.dto.request;

import com.bantads.gerente.enums.GerenteTipo;

public record AtualizaGerenteDTO(
        String nome,
        String email,
        String senha,
        String telefone,
        GerenteTipo tipo,
        String cpf
) {
        public AtualizaGerenteDTO {
                telefone = (telefone != null) ? telefone.replaceAll("\\D", "") : null;
        }
}
