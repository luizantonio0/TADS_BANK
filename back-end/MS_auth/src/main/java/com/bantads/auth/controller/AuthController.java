package com.bantads.auth.controller;

import com.bantads.auth.dto.LoginDTO;
import com.bantads.auth.service.AuthService;
import com.bantads.auth.service.JwtService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        token = token == null ? "" : token.replace("Bearer ", "");
        if (!token.isEmpty()) {

            var email = jwtService.extractUsername(token);
            var authentication = SecurityContextHolder.getContext().getAuthentication();

            if (email != null && authentication == null) {
                if (jwtService.isTokenValid(token)) {
                    return ResponseEntity.ok().build();
                }
            }

            return ResponseEntity.ok("Token is valid");
        }

        return ResponseEntity.status(401).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        if(authService.login(loginDTO.login(), loginDTO.senha())) {
            return ResponseEntity.ok("Login efetuado com sucesso");
        }
        return ResponseEntity.status(401).body("Usuário/Senha inválidos");
    }

}