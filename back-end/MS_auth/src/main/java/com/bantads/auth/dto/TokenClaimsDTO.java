package com.bantads.auth.dto;

public record TokenClaimsDTO (String cpf, String profile) {
    public TokenClaimsDTO {
        cpf = (cpf != null) ? cpf.replaceAll("\\D", "") : null;
    }
}
