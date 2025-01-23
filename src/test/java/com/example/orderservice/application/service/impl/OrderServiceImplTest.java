package com.example.orderservice.application.service.impl;

import com.example.orderservice.application.event.OrderStatusChangedEvent;
import com.example.orderservice.domain.order.Order;
import com.example.orderservice.domain.order.OrderStatus;
import com.example.orderservice.infrastructure.config.security.UserContextHelper;
import com.example.orderservice.infrastructure.mapper.OrderMapper;
import com.example.orderservice.infrastructure.repository.OrderRepository;
import com.example.orderservice.presentation.dto.order.OrderRequestDto;
import com.example.orderservice.presentation.dto.order.OrderResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderMapper mapper;

    @Mock
    private UserContextHelper userContextHelper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order mockOrder;
    private OrderRequestDto mockOrderRequestDto;
    private OrderResponseDto mockOrderResponseDto;

    @BeforeEach
    void setUp() {
        mockOrder = new Order();
        mockOrder.setId(1L);
        mockOrder.setCustomerName("user@example.com");
        mockOrder.setStatus(OrderStatus.PENDING);

        mockOrderRequestDto = new OrderRequestDto();
        mockOrderResponseDto = new OrderResponseDto();
    }

    @Test
    @DisplayName("Should create a new order and return response DTO")
    void createOrder_ValidRequest_ReturnsOrderResponseDto() {
        when(mapper.toEntity(any(OrderRequestDto.class))).thenReturn(mockOrder);
        when(repository.save(any(Order.class))).thenReturn(mockOrder);
        when(mapper.toDto(any(Order.class))).thenReturn(mockOrderResponseDto);

        OrderResponseDto result = orderService.createOrder(mockOrderRequestDto);

        assertNotNull(result);
        verify(repository).save(any(Order.class));
        verify(mapper).toDto(mockOrder);
    }

    @Test
    @DisplayName("Should update an existing order successfully")
    void updateOrder_ValidRequest_ReturnsUpdatedOrderResponseDto() {
        when(userContextHelper.getCurrentUserEmail()).thenReturn("user@example.com");
        when(repository.findByIdAndNotDeleted(eq(1L))).thenReturn(Optional.of(mockOrder));
        when(mapper.toEntity(any(OrderRequestDto.class))).thenReturn(mockOrder);
        when(repository.save(any(Order.class))).thenReturn(mockOrder);
        when(mapper.toDto(any(Order.class))).thenReturn(mockOrderResponseDto);

        OrderResponseDto result = orderService.updateOrder(1L, mockOrderRequestDto);

        assertNotNull(result);
        verify(repository).save(mockOrder);
        verify(mapper).toDto(mockOrder);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating a non-existent order")
    void updateOrder_NonExistentOrder_ThrowsEntityNotFoundException() {
        when(userContextHelper.getCurrentUserEmail()).thenReturn("user@example.com");
        when(repository.findByIdAndNotDeleted(eq(1L))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                orderService.updateOrder(1L, mockOrderRequestDto));

        verify(repository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should return an order by ID for the authenticated user")
    void getOrderById_ValidRequest_ReturnsOrderResponseDto() {
        when(userContextHelper.getCurrentUserEmail()).thenReturn("user@example.com");
        when(repository.findByIdAndNotDeleted(eq(1L))).thenReturn(Optional.of(mockOrder));
        when(mapper.toDto(any(Order.class))).thenReturn(mockOrderResponseDto);

        OrderResponseDto result = orderService.getOrderById(1L);

        assertNotNull(result);
        verify(repository).findByIdAndNotDeleted(1L);
        verify(mapper).toDto(mockOrder);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when order is not found by ID")
    void getOrderById_NonExistentOrder_ThrowsEntityNotFoundException() {
        when(userContextHelper.getCurrentUserEmail()).thenReturn("user@example.com");
        when(repository.findByIdAndNotDeleted(eq(1L))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                orderService.getOrderById(1L));

        verify(repository).findByIdAndNotDeleted(1L);
    }

    @Test
    @DisplayName("Should retrieve orders for admin based on filters")
    void getOrders_AsAdmin_ReturnsFilteredOrders() {
        when(userContextHelper.isAdmin()).thenReturn(true);
        when(repository.findOrdersByFilters(any(), any(), any()))
                .thenReturn(Collections.singletonList(mockOrder));
        when(mapper.toDto(any(Order.class))).thenReturn(mockOrderResponseDto);

        List<OrderResponseDto> result = orderService.getOrders(OrderStatus.PENDING, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findOrdersByFilters(any(), any(), any());
        verify(mapper).toDto(mockOrder);
    }

    @Test
    @DisplayName("Should retrieve orders for a user based on filters")
    void getOrders_AsUser_ReturnsFilteredOrders() {
        when(userContextHelper.isAdmin()).thenReturn(false);
        when(userContextHelper.getCurrentUserEmail()).thenReturn("user@example.com");
        when(repository.findOrdersByFiltersForUser(anyString(), any(), any(), any()))
                .thenReturn(Collections.singletonList(mockOrder));
        when(mapper.toDto(any(Order.class))).thenReturn(mockOrderResponseDto);

        List<OrderResponseDto> result = orderService.getOrders(OrderStatus.PENDING, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findOrdersByFiltersForUser(eq("user@example.com"), any(), any(), any());
        verify(mapper).toDto(mockOrder);
    }

    @Test
    @DisplayName("Should mark an order as deleted successfully")
    void deleteOrder_ValidRequest_MarksOrderAsDeleted() {
        when(userContextHelper.getCurrentUserEmail()).thenReturn("user@example.com");
        when(repository.findByIdAndNotDeleted(eq(1L))).thenReturn(Optional.of(mockOrder));

        orderService.deleteOrder(1L);

        verify(repository).save(mockOrder);
        assertTrue(mockOrder.getIsDeleted());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting a non-existent order")
    void deleteOrder_NonExistentOrder_ThrowsEntityNotFoundException() {
        when(userContextHelper.getCurrentUserEmail()).thenReturn("user@example.com");
        when(repository.findByIdAndNotDeleted(eq(1L))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                orderService.deleteOrder(1L));

        verify(repository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should publish OrderStatusChangedEvent when order status is updated")
    void updateOrder_ValidRequest_PublishesOrderStatusChangedEvent() {
        when(userContextHelper.getCurrentUserEmail()).thenReturn("user@example.com");
        when(repository.findByIdAndNotDeleted(eq(1L))).thenReturn(Optional.of(mockOrder));

        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setCustomerName("user@example.com");
        updatedOrder.setStatus(OrderStatus.CONFIRMED);
        when(mapper.toEntity(any(OrderRequestDto.class))).thenReturn(updatedOrder);
        when(repository.save(any(Order.class))).thenReturn(updatedOrder);

        orderService.updateOrder(1L, mockOrderRequestDto);

        verify(eventPublisher).publishEvent(any(OrderStatusChangedEvent.class));
    }
}
