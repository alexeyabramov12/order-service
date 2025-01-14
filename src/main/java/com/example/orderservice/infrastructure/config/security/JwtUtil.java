package com.example.orderservice.infrastructure.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Utility class for handling JWT (JSON Web Token) operations such as token generation,
 * validation, and extracting claims (e.g., username and roles).
 */
@Component
public class JwtUtil {

    /**
     * Secret key used for signing the JWT tokens.
     * Injected from application properties.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Token expiration time in milliseconds.
     * Injected from application properties.
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Generates a JWT token for the specified username and roles.
     *
     * @param username the username for which the token is being generated
     * @param roles    the list of roles assigned to the user
     * @return the generated JWT token as a {@link String}
     */
    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the username (subject) from the provided JWT token.
     *
     * @param token the JWT token
     * @return the username (subject) contained in the token
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validates the provided JWT token by checking its signature and expiration.
     *
     * @param token the JWT token to validate
     * @return {@code true} if the token is valid, {@code false} otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
