package com.example.orderservice.application.service.impl;

import com.example.orderservice.domain.auth.User;
import com.example.orderservice.infrastructure.config.security.JwtUtil;
import com.example.orderservice.infrastructure.repository.UserRepository;
import com.example.orderservice.presentation.dto.auth.AuthDto;
import com.example.orderservice.presentation.dto.auth.AuthResponseDto;
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

    private AuthDto validAuthDto;
    private AuthDto invalidPasswordAuthDto;
    private User validUser;

    @BeforeEach
    void setUp() {
        validAuthDto = new AuthDto("user@example.com", "password123");
        invalidPasswordAuthDto = new AuthDto("user@example.com", "wrongpassword");

        validUser = new User();
        validUser.setEmail("user@example.com");
        validUser.setPassword("$2a$10$yQgR.Xx2WbFjHFABmBjX3uzk6QEYJBB9XJe3Yr5ALP9P9wMLkTPFe");
    }

    @Test
    @DisplayName("Should return a token when credentials are valid")
    void login_ValidCredentials_ReturnsToken() {
        when(userRepository.findByEmail(validAuthDto.getEmail())).thenReturn(java.util.Optional.of(validUser));
        when(encoder.matches(validAuthDto.getPassword(), validUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(validUser)).thenReturn("mocked-jwt-token");

        AuthResponseDto response = authService.login(validAuthDto);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        verify(userRepository, times(1)).findByEmail(validAuthDto.getEmail());
        verify(encoder, times(1))
                .matches(validAuthDto.getPassword(), validUser.getPassword());
        verify(jwtUtil, times(1)).generateToken(validUser);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user is not found")
    void login_UserNotFound_ThrowsException() {
        when(userRepository.findByEmail(validAuthDto.getEmail())).thenReturn(java.util.Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.login(validAuthDto)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(validAuthDto.getEmail());
        verifyNoInteractions(encoder);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when password is invalid")
    void login_InvalidPassword_ThrowsException() {
        when(userRepository.findByEmail(invalidPasswordAuthDto.getEmail()))
                .thenReturn(java.util.Optional.of(validUser));
        when(encoder.matches(invalidPasswordAuthDto.getPassword(), validUser.getPassword()))
                .thenReturn(false);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authService.login(invalidPasswordAuthDto)
        );

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(invalidPasswordAuthDto.getEmail());
        verify(encoder, times(1))
                .matches(invalidPasswordAuthDto.getPassword(), validUser.getPassword());
        verifyNoInteractions(jwtUtil);
    }
}
