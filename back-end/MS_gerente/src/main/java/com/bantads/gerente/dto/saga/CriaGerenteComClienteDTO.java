package com.bantads.gerente.dto.saga;

import com.bantads.gerente.enums.GerenteTipo;

public record CriaGerenteComClienteDTO(
        String nome,
        String email,
        String cpf,
        GerenteTipo tipo,
        String cpfCliente
) {
        public CriaGerenteComClienteDTO {
                cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
                cpfCliente = (cpfCliente != null) ? cpfCliente.replaceAll("\\D", "") : null;
        }
}
