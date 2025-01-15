package com.example.orderservice.presentation.controller;

import com.example.orderservice.application.service.AuthService;
import com.example.orderservice.presentation.dto.auth.AuthenticateDto;
import com.example.orderservice.presentation.dto.auth.AuthenticateResponseDto;
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
@AutoConfigureMockMvc()
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private ObjectWriter ow;

    private AuthenticateDto validAuthenticateDto;
    private AuthenticateResponseDto validResponseDto;

    @BeforeEach
    void setUp() {
        ow = new ObjectMapper().configure(SerializationFeature.WRAP_ROOT_VALUE, false).writer().withDefaultPrettyPrinter();

        validAuthenticateDto = new AuthenticateDto("user@example.com", "password123");
        validResponseDto = new AuthenticateResponseDto("mocked-jwt-token");
    }

    @Test
    @DisplayName("Should return JWT token when login is successful")
    void login_Success_ReturnsToken() throws Exception {
        ArgumentCaptor<AuthenticateDto> captor = ArgumentCaptor.forClass(AuthenticateDto.class);
        when(authService.login(Mockito.any(AuthenticateDto.class))).thenReturn(validResponseDto);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(validAuthenticateDto)))
                .andExpect(status().isOk());

        verify(authService, times(1)).login(captor.capture());
        AuthenticateDto capturedDto = captor.getValue();
        assertEquals(validAuthenticateDto.getEmail(), capturedDto.getEmail());
        assertEquals(validAuthenticateDto.getPassword(), capturedDto.getPassword());
    }


    @Test
    @DisplayName("Should return 400 when input data is invalid")
    void login_InvalidInput_ReturnsBadRequest() throws Exception {
        AuthenticateDto invalidDto = new AuthenticateDto("", "");

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
                        .content(ow.writeValueAsString(validAuthenticateDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }
}
