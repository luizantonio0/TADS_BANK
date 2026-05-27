package com.bantads.auth.controller;

import com.bantads.auth.dto.LoginDTO;
import com.bantads.auth.dto.LoginResponseDTO;
import com.bantads.auth.dto.LogoutResponseDTO;
import com.bantads.auth.dto.TokenClaimsDTO;
import com.bantads.auth.exception.UnauthorizedException;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.service.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private JwtService jwtService;
    private AuthService authService;

    public AuthController(JwtService jwtService, AuthService authService) {
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenClaimsDTO> validateToken(@RequestHeader("Authorization") String token) {
        token = token == null ? "" : token.replace("Bearer ", "");
        if (!token.isEmpty()) {
            var user = jwtService.parseToken(token);
            if(user != null) {
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/logout")
    public CompletableFuture<ResponseEntity<LogoutResponseDTO>> login(@RequestHeader("Authorization") String token) throws UnauthorizedException, JsonProcessingException {
        return authService.startLogout(token)
                .thenApply(ResponseEntity::ok)
                .orTimeout(15, TimeUnit.SECONDS);
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<LoginResponseDTO>> login(@RequestBody LoginDTO loginDTO) throws UnauthorizedException, JsonProcessingException {
        return authService.startLogin(loginDTO.login(), loginDTO.senha())
                .thenApply(ResponseEntity::ok)
                .orTimeout(15, TimeUnit.SECONDS);
    }

    @GetMapping("/reboot")
    public ResponseEntity<?> reboot() {
        authService.reboot();
        return ResponseEntity.ok("");
    }

}