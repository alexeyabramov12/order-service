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
 * This class sets up the security policies and components required for authentication
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
     * This method:
     * <ul>
     *     <li>Disables CSRF (as the application uses stateless authentication).</li>
     *     <li>Defines access control policies based on roles and endpoints.</li>
     *     <li>Adds a custom {@link JwtAuthenticationFilter} before the {@link UsernamePasswordAuthenticationFilter}.</li>
     *     <li>Enforces stateless session management.</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} object to configure security settings
     * @return a fully configured {@link SecurityFilterChain} bean
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/orders/**").hasAnyAuthority("User", "Admin")
                        .requestMatchers("/admin/**").hasAuthority("Admin")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * Exposes the {@link AuthenticationManager} as a bean for use in authentication processes.
     * <p>
     * The {@link AuthenticationManager} is automatically configured using the application's
     * security configuration (e.g., user details, password encoder).
     *
     * @param configuration the {@link AuthenticationConfiguration} provided by Spring Security
     * @return a fully configured {@link AuthenticationManager} bean
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Configures a password encoder bean for hashing passwords.
     * <p>
     * The {@link BCryptPasswordEncoder} is a strong password hashing algorithm and
     * is recommended for modern applications.
     *
     * @return a {@link PasswordEncoder} bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
