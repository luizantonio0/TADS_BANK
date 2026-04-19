package main.java.MS_Gerente.bantads.dto.response;

import main.java.MS_Gerente.bantads.enums.GerenteTipo;
import main.java.MS_Gerente.bantads.model.Gerente;

public record GerenteAtualizadoDTO(
        String nome,
        String email,
        String senha,
        GerenteTipo tipo
) {
    public GerenteAtualizadoDTO(Gerente gerente) {
        this(
                gerente.getNome(),
                gerente.getEmail(),
                gerente.getSenha(),
                GerenteTipo.valueOf(gerente.getTipo())
        );
    }
}
