package com.example.orderservice.presentation.controller;

import com.example.orderservice.application.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @WithMockUser(roles = "Admin")
    @DisplayName("Should return application metrics successfully")
    void getCustomMetrics_Success() throws Exception {
        when(orderService.getOrders(any(), any(), any())).thenReturn(List.of());
        mockMvc.perform(get("/orders")
                .with(csrf()));

        when(orderService.getOrders(any(), any(), any())).thenThrow(new RuntimeException());
        mockMvc.perform(get("/orders")
                .with(csrf()));

        mockMvc.perform(get("/metrics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_requests").value(2.0))
                .andExpect(jsonPath("$.successful_requests").value(1.0))
                .andExpect(jsonPath("$.failed_requests").value(1.0));

    }

    @Test
    @DisplayName("Should return 403 for unauthorized user roles")
    void getCustomMetrics_Forbidden() throws Exception {
        mockMvc.perform(get("/metrics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "User")
    @DisplayName("Should return 403 for role User")
    void getCustomMetrics_Unauthenticated() throws Exception {
        mockMvc.perform(get("/metrics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
