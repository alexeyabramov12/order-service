package com.example.orderservice.application.service.impl;

import com.example.orderservice.application.service.AuthService;
import com.example.orderservice.domain.auth.User;
import com.example.orderservice.infrastructure.config.security.JwtUtil;
import com.example.orderservice.infrastructure.repository.UserRepository;
import com.example.orderservice.presentation.dto.auth.AuthDto;
import com.example.orderservice.presentation.dto.auth.AuthResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(AuthDto authDto) {

        final User user = userRepository.findByEmail(authDto.getEmail()).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (encoder.matches(authDto.getPassword(), user.getPassword())) {
            return new AuthResponseDto(jwtUtil.generateToken(user));
        } else {
            log.info("Invalid password");
            throw new BadCredentialsException("Invalid password");
        }
    }
}
