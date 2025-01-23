package com.example.orderservice.presentation.controller.impl;

import com.example.orderservice.application.service.AuthService;
import com.example.orderservice.presentation.controller.AuthControllerApi;
import com.example.orderservice.presentation.dto.auth.AuthenticateDto;
import com.example.orderservice.presentation.dto.auth.AuthenticateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthenticateResponseDto> login(AuthenticateDto authenticateDto) {
        return ResponseEntity.ok(authService.login(authenticateDto));
    }
}
