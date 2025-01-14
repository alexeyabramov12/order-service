package com.example.orderservice.infrastructure.config.security;

import com.example.orderservice.domain.auth.Role;
import com.example.orderservice.domain.auth.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
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
     * Token expiration time in seconds.
     * Injected from application properties.
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Generates a JWT token for the given user.
     * <p>
     * The token includes user details (e.g., email, roles, and ID) as claims
     * and is signed using the secret key and HMAC-SHA256 algorithm.
     *
     * @param user the {@link User} entity for which the token is being generated
     * @return the generated JWT token as a {@link String}
     */
    public String generateToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("roles", getTypesNames(user.getRoles()));
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());

        final ZonedDateTime now = ZonedDateTime.now();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(now.plusSeconds(expiration).toInstant()))
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

    /**
     * Converts a list of {@link Role} entities to a list of role names as {@link String}.
     * <p>
     * This method is used to extract role names from the user's roles for inclusion
     * in the JWT token.
     *
     * @param roles the list of {@link Role} entities
     * @return a list of role names as {@link String}
     */
    private List<String> getTypesNames(@NonNull List<Role> roles) {
        List<String> result = new ArrayList<>();
        roles.forEach(t -> result.add(t.getName()));
        return result;
    }
}
