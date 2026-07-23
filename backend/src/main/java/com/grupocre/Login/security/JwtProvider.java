package com.grupocre.Login.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtProvider(@Value("${app.jwt.secret}") String secret,
                       @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        // Opción simplificada: directamente usar secret como bytes (sin Base64)
        // La clave debe tener al menos 256 bits para HS256 (32 caracteres)
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(secret.getBytes())
        );
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMinutes * 60 * 1000;
    }

    public String generateToken(Long userId, String username, String email, List<String> roles) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .claim("userId", userId)
                .claim("email", email)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)    // JJWT 0.12 permite signWith(SecretKey) sin algoritmo explícito
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)   // Configura la clave para verificación
                .build()
                .parseSignedClaims(token)   // Devuelve Jws<Claims>
                .getPayload();              // Extrae los Claims
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Date getExpirationDate(String token) {
        return parseToken(token).getExpiration();
    }
}