package com.gemsflare.gemsflare.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    private static final String SECRET_KEY = "ThisIsASecretKeyForJWTGeneration244532";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(UUID userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
        return UUID.fromString(claims.getSubject());
    }

    public String validateAndRenewToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();

            Date expirationDate = claims.getExpiration();
            long timeRemaining = expirationDate.getTime() - System.currentTimeMillis();

            if (timeRemaining <= 0) {
                return null;
            }

            if (timeRemaining <= 30 * 60 * 1000) {
                UUID userId = UUID.fromString(claims.getSubject());
                return generateToken(userId);
            }

            return token;

        } catch (JwtException e) {
            return null;
        }
    }

    public Key getKey() {
        return key;
    }
}