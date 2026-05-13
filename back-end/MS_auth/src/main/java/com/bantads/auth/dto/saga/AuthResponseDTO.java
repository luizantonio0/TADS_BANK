package com.bantads.auth.dto.saga;

public record AuthResponseDTO(String accessToken, String tokenType, String profile) {
    
}
