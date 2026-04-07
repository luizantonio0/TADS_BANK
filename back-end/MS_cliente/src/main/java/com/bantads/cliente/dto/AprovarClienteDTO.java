package com.bantads.cliente.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record AprovarClienteDTO(
        String cpf
) implements Serializable { }
