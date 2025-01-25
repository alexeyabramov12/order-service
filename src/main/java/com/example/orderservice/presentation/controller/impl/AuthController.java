package com.example.orderservice.presentation.controller.impl;

import com.example.orderservice.application.service.AuthService;
import com.example.orderservice.presentation.controller.AuthControllerApi;
import com.example.orderservice.presentation.dto.auth.AuthDto;
import com.example.orderservice.presentation.dto.auth.AuthResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthControllerApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponseDto> login(AuthDto authenticateDto) {
        return ResponseEntity.ok(authService.login(authenticateDto));
    }
}
