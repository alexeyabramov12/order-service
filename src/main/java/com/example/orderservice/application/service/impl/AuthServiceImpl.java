package com.example.orderservice.application.service.impl;

import com.example.orderservice.application.service.AuthService;
import com.example.orderservice.domain.auth.User;
import com.example.orderservice.infrastructure.config.security.JwtUtil;
import com.example.orderservice.infrastructure.repository.UserRepository;
import com.example.orderservice.presentation.dto.auth.AuthenticateDto;
import com.example.orderservice.presentation.dto.auth.AuthenticateResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    public AuthenticateResponseDto login(AuthenticateDto authenticateDto) {

        final User user = userRepository.findByEmail(authenticateDto.getEmail()).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (encoder.matches(authenticateDto.getPassword(), user.getPassword())) {
            return new AuthenticateResponseDto(jwtUtil.generateToken(user));
        } else {
            log.info("Invalid password");
            throw new BadCredentialsException("Invalid password");
        }
    }
}
