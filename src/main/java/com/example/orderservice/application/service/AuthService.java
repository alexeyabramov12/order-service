package com.example.orderservice.application.service;

import com.example.orderservice.presentation.dto.auth.AuthenticateDto;
import com.example.orderservice.presentation.dto.auth.AuthenticateResponseDto;

/**
 * Service interface for handling authentication-related operations.
 * <p>
 * This service is responsible for user login and generating authentication tokens.
 */
public interface AuthService {

    /**
     * Authenticates a user based on the provided credentials and generates an authentication token.
     *
     * @param authenticateDto the {@link AuthenticateDto} object containing the user's email and password.
     * @return an {@link AuthenticateResponseDto} object containing the generated authentication token and other user-related information.
     * @throws org.springframework.security.authentication.BadCredentialsException if the provided credentials are invalid.
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if the user with the provided email does not exist.
     */
    AuthenticateResponseDto login(AuthenticateDto authenticateDto);
}
