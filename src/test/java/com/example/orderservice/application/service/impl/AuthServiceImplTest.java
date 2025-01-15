package com.example.orderservice.application.service.impl;

import com.example.orderservice.domain.auth.User;
import com.example.orderservice.infrastructure.config.security.JwtUtil;
import com.example.orderservice.infrastructure.repository.UserRepository;
import com.example.orderservice.presentation.dto.auth.AuthenticateDto;
import com.example.orderservice.presentation.dto.auth.AuthenticateResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthenticateDto validAuthenticateDto;
    private AuthenticateDto invalidPasswordAuthenticateDto;
    private User validUser;

    @BeforeEach
    void setUp() {
        validAuthenticateDto = new AuthenticateDto("user@example.com", "password123");
        invalidPasswordAuthenticateDto = new AuthenticateDto("user@example.com", "wrongpassword");

        validUser = new User();
        validUser.setEmail("user@example.com");
        validUser.setPassword("$2a$10$yQgR.Xx2WbFjHFABmBjX3uzk6QEYJBB9XJe3Yr5ALP9P9wMLkTPFe");
    }

    @Test
    @DisplayName("Should return a token when credentials are valid")
    void login_ValidCredentials_ReturnsToken() {
        when(userRepository.findByEmail(validAuthenticateDto.getEmail())).thenReturn(java.util.Optional.of(validUser));
        when(encoder.matches(validAuthenticateDto.getPassword(), validUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(validUser)).thenReturn("mocked-jwt-token");

        AuthenticateResponseDto response = authService.login(validAuthenticateDto);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        verify(userRepository, times(1)).findByEmail(validAuthenticateDto.getEmail());
        verify(encoder, times(1))
                .matches(validAuthenticateDto.getPassword(), validUser.getPassword());
        verify(jwtUtil, times(1)).generateToken(validUser);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found")
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(validAuthenticateDto.getEmail())).thenReturn(java.util.Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.login(validAuthenticateDto)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(validAuthenticateDto.getEmail());
        verifyNoInteractions(encoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when password is invalid")
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByEmail(invalidPasswordAuthenticateDto.getEmail()))
                .thenReturn(java.util.Optional.of(validUser));
        when(encoder.matches(invalidPasswordAuthenticateDto.getPassword(), validUser.getPassword()))
                .thenReturn(false);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(invalidPasswordAuthenticateDto)
        );

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(invalidPasswordAuthenticateDto.getEmail());
        verify(encoder, times(1))
                .matches(invalidPasswordAuthenticateDto.getPassword(), validUser.getPassword());
        verifyNoInteractions(jwtUtil);
    }
}
