package com.example.orderservice.presentation.controller;

import com.example.orderservice.application.service.OrderService;
import com.example.orderservice.domain.order.OrderStatus;
import com.example.orderservice.presentation.dto.order.OrderRequestDto;
import com.example.orderservice.presentation.dto.order.OrderResponseDto;
import com.example.orderservice.presentation.dto.product.ProductRequestDto;
import com.example.orderservice.presentation.dto.product.ProductResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private OrderRequestDto validRequestDto;
    private OrderResponseDto validResponseDto;

    @BeforeEach
    void setUp() {
        ProductRequestDto productRequest = new ProductRequestDto(1L, "Product1", 100.0, 2);
        ProductResponseDto productResponse = new ProductResponseDto(1L, "Product1", 100.0, 2);

        validRequestDto = new OrderRequestDto(
                "customer@example.com",
                OrderStatus.PENDING.toString(),
                199.99,
                List.of(productRequest)
        );

        validResponseDto = new OrderResponseDto(
                1L,
                "customer@example.com",
                OrderStatus.PENDING.toString(),
                199.99,
                List.of(productResponse)
        );
    }

    @Test
    @WithMockUser(roles = "User")
    @DisplayName("Should create a new order successfully")
    void createOrder_Success() throws Exception {
        when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(validResponseDto);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("customer@example.com"))
                .andExpect(jsonPath("$.products[0].name").value("Product1"));

        verify(orderService, times(1)).createOrder(any(OrderRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "User")
    @DisplayName("Should return 400 when invalid order data is provided")
    void createOrder_InvalidData_ReturnsBadRequest() throws Exception {
        OrderRequestDto invalidRequestDto = new OrderRequestDto("", null, null, null);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(orderService);
    }

    @Test
    @WithMockUser(roles = "User")
    @DisplayName("Should update an existing order successfully")
    void updateOrder_Success() throws Exception {
        when(orderService.updateOrder(eq(1L), any(OrderRequestDto.class))).thenReturn(validResponseDto);

        mockMvc.perform(put("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.products[0].name").value("Product1"));

        verify(orderService, times(1)).updateOrder(eq(1L), any(OrderRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "User")
    @DisplayName("Should return 404 when updating non-existent order")
    void updateOrder_NotFound() throws Exception {
        Long orderId = 999L;
        String errorMessage = String.format("Order with ID %d not found or you do not have access rights", orderId);
        when(orderService.updateOrder(eq(orderId), any(OrderRequestDto.class)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        mockMvc.perform(put("/orders/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errorMessage));

        verify(orderService, times(1)).updateOrder(eq(orderId), any(OrderRequestDto.class));
    }

    @Test
    @WithMockUser(roles = "User")
    @DisplayName("Should retrieve an order by ID")
    void getOrderById_Success() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(validResponseDto);

        mockMvc.perform(get("/orders/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.products[0].name").value("Product1"));

        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    @WithMockUser(roles = "User")
    @DisplayName("Should return 404 when retrieving non-existent order")
    void getOrderById_NotFound() throws Exception {
        Long orderId = 999L;
        when(orderService.getOrderById(orderId)).thenThrow(new EntityNotFoundException("Order not found"));

        mockMvc.perform(get("/orders/999").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found"));

        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    @WithMockUser(roles = "User")
    @DisplayName("Should delete an order successfully")
    void deleteOrder_Success() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/orders/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder(1L);
    }

    @Test
    @WithMockUser(roles = "User")
    @DisplayName("Should return 404 when deleting non-existent order")
    void deleteOrder_NotFound() throws Exception {
        Long orderId = 999L;
        doThrow(new EntityNotFoundException("Order not found")).when(orderService).deleteOrder(orderId);

        mockMvc.perform(delete("/orders/999").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Order not found"));

        verify(orderService, times(1)).deleteOrder(orderId);
    }

    @Test
    @WithMockUser(roles = "Admin")
    @DisplayName("Should retrieve all orders as Admin")
    void getOrdersAsAdmin_Success() throws Exception {
        when(orderService.getOrders(any(), any(), any())).thenReturn(List.of(validResponseDto));

        mockMvc.perform(get("/orders")
                        .param("status", OrderStatus.PENDING.name())
                        .param("minPrice", "100")
                        .param("maxPrice", "200")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].customerName").value("customer@example.com"));

        verify(orderService, times(1)).getOrders(any(), any(), any());
    }

    @Test
    @WithMockUser(roles = "Admin")
    @DisplayName("Should handle empty orders list")
    void getOrders_EmptyList() throws Exception {
        when(orderService.getOrders(any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(orderService, times(1)).getOrders(any(), any(), any());
    }
}
