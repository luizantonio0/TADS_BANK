package com.bantads.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bantads.auth.document.Token;
import com.bantads.auth.dto.TokenClaimsDTO;
import com.bantads.auth.repository.TokenRepository;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Autowired private TokenRepository tokenRepository;

    private boolean tokenExists(String token) {
        return tokenRepository.existsById(token);
    }

    public void saveToken(String cpf, String token) {
        tokenRepository.save(new Token(cpf, token));
    }

    public void revokeToken(String token) {
        tokenRepository.deleteById(token);
    }

    public void revokeAllTokens(String cpf) {
        tokenRepository.deleteByCpf(cpf);
    }

    public TokenClaimsDTO extractUsuario(String token) {
        return extractClaim(token, (c) ->
            new TokenClaimsDTO(c.getSubject(), c.get("profile", String.class))
        );
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String email, String profile) {
        return buildToken(email, profile, jwtExpiration);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildToken(String email, String profile, long expiration) {
        return Jwts
                .builder()
                .claims(Map.of("profile", profile))
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public TokenClaimsDTO parseToken(String token) {
        token = token.replace("Bearer ", "").trim();
        if (isTokenExpired(token)) {
            return null;
        }
        return extractUsuario(token);
    }

    private boolean isTokenExpired(String token) {
        var expiration = extractExpiration(token);
        return expiration.before(new Date()) || !tokenExists(token);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}