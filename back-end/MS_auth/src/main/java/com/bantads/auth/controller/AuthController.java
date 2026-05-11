package com.bantads.auth.controller;

import com.bantads.auth.dto.LoginDTO;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        token = token == null ? "" : token.replace("Bearer ", "");
        if (!token.isEmpty()) {
            var user = jwtService.parseToken(token);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        if(authService.login(loginDTO.login(), loginDTO.senha()) != null) {
            return ResponseEntity.ok("Login efetuado com sucesso");
        }
        return ResponseEntity.status(401).body("Usuário/Senha inválidos");
    }

}