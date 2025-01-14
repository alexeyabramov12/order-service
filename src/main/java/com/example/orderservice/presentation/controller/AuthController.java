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

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticateResponseDto> login(@Valid @RequestBody AuthenticateDto authenticateDto) {
        return ResponseEntity.ok(authService.login(authenticateDto));
    }
}
