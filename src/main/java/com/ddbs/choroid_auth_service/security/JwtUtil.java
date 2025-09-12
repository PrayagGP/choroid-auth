package com.ddbs.choroid_auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    
    private final SecretKey secretKey;
    private final long jwtExpiration;
    private final long refreshExpiration;
    
    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long jwtExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration
    ) {
        // Ensure the secret is at least 256 bits (32 bytes) for HS256
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            log.warn("JWT secret key is less than 256 bits. Consider using a longer key.");
            // Pad the secret to meet minimum requirements
            secret = secret + "0".repeat(32 - secret.getBytes(StandardCharsets.UTF_8).length);
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiration = jwtExpiration;
        this.refreshExpiration = refreshExpiration;
        log.info("JWT Utility initialized with expiration: {} ms", jwtExpiration);
    }
    
    /**
     * Generate JWT token with username
     * @param username the username
     * @return JWT token
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username, jwtExpiration);
    }
    
    /**
     * Generate refresh token
     * @param username the username
     * @return refresh token
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, username, refreshExpiration);
    }
    
    /**
     * Generate token with custom claims
     * @param extraClaims additional claims
     * @param username the username
     * @return JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return createToken(extraClaims, username, jwtExpiration);
    }
    
    /**
     * Extract username from token
     * @param token JWT token
     * @return username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extract expiration date from token
     * @param token JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extract a specific claim from token
     * @param token JWT token
     * @param claimsResolver function to extract claim
     * @param <T> claim type
     * @return extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Validate token against username
     * @param token JWT token
     * @param username username to validate against
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if token is expired
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Check if token is valid (not expired and parseable)
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Create token with claims, subject and expiration
     * @param claims token claims
     * @param subject token subject (username)
     * @param expiration expiration time in milliseconds
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }
    
    /**
     * Extract all claims from token
     * @param token JWT token
     * @return claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

