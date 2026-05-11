package com.bantads.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponseDTO(
    @JsonProperty("access_token") String accessToken, 
    @JsonProperty("token_type") String tokenType, 
    String tipo,
    LoginUsuarioResponseDTO usuario
) {}