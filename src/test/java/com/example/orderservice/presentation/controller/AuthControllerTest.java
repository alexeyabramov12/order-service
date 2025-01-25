package com.example.orderservice.presentation.controller;

import com.example.orderservice.application.service.AuthService;
import com.example.orderservice.presentation.dto.auth.AuthDto;
import com.example.orderservice.presentation.dto.auth.AuthResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc()
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private ObjectWriter ow;

    private AuthDto validAuthDto;
    private AuthResponseDto validResponseDto;

    @BeforeEach
    void setUp() {
        ow = new ObjectMapper().configure(SerializationFeature.WRAP_ROOT_VALUE, false).writer().withDefaultPrettyPrinter();

        validAuthDto = new AuthDto("user@example.com", "password123");
        validResponseDto = new AuthResponseDto("mocked-jwt-token");
    }

    @Test
    @DisplayName("Should return JWT token when login is successful")
    void login_Success_ReturnsToken() throws Exception {
        ArgumentCaptor<AuthDto> captor = ArgumentCaptor.forClass(AuthDto.class);
        when(authService.login(Mockito.any(AuthDto.class))).thenReturn(validResponseDto);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(validAuthDto)))
                .andExpect(status().isOk());

        verify(authService, times(1)).login(captor.capture());
        AuthDto capturedDto = captor.getValue();
        assertEquals(validAuthDto.getEmail(), capturedDto.getEmail());
        assertEquals(validAuthDto.getPassword(), capturedDto.getPassword());
    }


    @Test
    @DisplayName("Should return 400 when input data is invalid")
    void login_InvalidInput_ReturnsBadRequest() throws Exception {
        AuthDto invalidDto = new AuthDto("", "");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authService);
    }

    @Test
    @DisplayName("Should return 401 when credentials are invalid")
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        when(authService.login(any())).thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(validAuthDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }
}
