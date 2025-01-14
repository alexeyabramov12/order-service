package com.example.orderservice.presentation.controller;

import com.example.orderservice.application.service.AuthService;
import com.example.orderservice.presentation.dto.auth.AuthenticateDto;
import com.example.orderservice.presentation.dto.auth.AuthenticateResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для аутентификации пользователей.
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Эндпоинт для выполнения входа.
     *
     * @param authenticateDto DTO с данными аутентификации
     * @return JWT токен, если вход успешен
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticateResponseDto> login(@Valid @RequestBody AuthenticateDto authenticateDto) {
        return ResponseEntity.ok(authService.login(authenticateDto));
    }
}

