package com.bantads.gerente.dto.request;

public record AtualizaGerenteDTO(
        String nome,
        String email,
        String senha,
        String telefone
) {
        public AtualizaGerenteDTO {
                telefone = (telefone != null) ? telefone.replaceAll("\\D", "") : null;
        }
}
