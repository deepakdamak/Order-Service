package com.order.orderservice.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        if (secret == null || secret.length() < 32) {
            log.error("JWT secret is too short or null. Minimum 32 chars required.");
            throw new IllegalArgumentException("JWT secret must be at least 32 characters");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logTokenException(e, token);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("Invalid JWT token");
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            throw new JwtException("Invalid JWT signature");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            throw new JwtException("JWT claims string is empty");
        }
    }

    private void logTokenException(Exception e, String token) {
        String tokenPreview = token.length() > 20
                ? token.substring(0, 20) + "..."
                : token;

        if (e instanceof ExpiredJwtException) {
            log.warn("JWT token expired: {}", tokenPreview);
        } else if (e instanceof MalformedJwtException) {
            log.warn("Invalid JWT token format: {}", tokenPreview);
        } else if (e instanceof SignatureException) {
            log.warn("Invalid JWT signature: {}", tokenPreview);
        } else if (e instanceof IllegalArgumentException) {
            log.warn("JWT claims string is empty: {}", tokenPreview);
        } else {
            log.error("JWT validation error: {}", e.getMessage(), e);
        }
    }

    public String refreshToken(String token) {
        if (validateToken(token)) {
            String username = extractUsername(token);
            return generateToken(username);
        }
        throw new JwtException("Cannot refresh invalid token");
    }

    public long getRemainingExpirationTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            return 0;
        }
    }
}