package com.example.orderservice.infrastructure.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter responsible for handling JWT authentication for every incoming HTTP request.
 * It extracts the JWT token from the `Authorization` header, validates it, and sets the
 * authentication context for the current user if the token is valid.
 * <p>
 * This filter extends {@link OncePerRequestFilter} to ensure that it is executed only once
 * per request.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Utility class for handling JWT operations such as extracting the username
     * and validating the token.
     */
    private final JwtUtil jwtUtil;

    /**
     * Custom implementation of {@link org.springframework.security.core.userdetails.UserDetailsService}
     * to load user details based on the username (email).
     */
    private final JwtUserDetailsService userDetailsService;

    /**
     * Filters each request to handle JWT authentication.
     *
     * @param request     the {@link HttpServletRequest} object containing client request data
     * @param response    the {@link HttpServletResponse} object to send response data
     * @param filterChain the {@link FilterChain} for invoking the next filter in the chain
     * @throws ServletException if an error occurs during servlet processing
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // Retrieve the Authorization header
        String authHeader = request.getHeader("Authorization");

        // Check if the header contains a Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            // If username is valid and no authentication exists in the context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from the database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate the token
                if (jwtUtil.validateToken(token)) {
                    // Create an authentication object and set it in the security context
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        // Pass the request and response to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
