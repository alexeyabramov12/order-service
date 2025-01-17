package com.example.orderservice.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for the application.
 * <p>
 * This class configures the security policies and components required for authentication
 * and authorization, including JWT-based authentication, role-based access control,
 * and stateless session management.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Custom filter for handling JWT authentication.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the security filter chain for the application.
     * <p>
     * This method performs the following tasks:
     * <ul>
     *     <li>Disables CSRF protection since the application uses stateless authentication.</li>
     *     <li>Defines access control policies for various endpoints based on user roles.</li>
     *     <li>Adds a custom {@link JwtAuthenticationFilter} before the {@link UsernamePasswordAuthenticationFilter}
     *         to handle JWT validation and authentication.</li>
     *     <li>Enforces stateless session management by disabling session creation.</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} object used to configure security settings
     * @return a configured {@link SecurityFilterChain} bean
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/orders").hasAnyRole("User", "Admin")
                        .requestMatchers("/orders/**").hasAnyRole("User", "Admin")
                        .anyRequest().authenticated()
                )
                // Add the custom JWT authentication filter before the UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Configure the application to use stateless session management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * Exposes the {@link AuthenticationManager} as a Spring bean.
     * <p>
     * The {@link AuthenticationManager} is automatically configured using the application's
     * security configuration, including the user details service and password encoder.
     *
     * @param configuration the {@link AuthenticationConfiguration} provided by Spring Security
     * @return a fully configured {@link AuthenticationManager} bean
     * @throws Exception if an error occurs while building the {@link AuthenticationManager}
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Configures a password encoder bean for hashing and verifying passwords.
     * <p>
     * This method provides a {@link BCryptPasswordEncoder}, which is a strong password hashing algorithm
     * recommended for modern applications.
     *
     * @return a {@link PasswordEncoder} bean used for password hashing
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
