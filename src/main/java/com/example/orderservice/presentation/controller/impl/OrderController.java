package com.example.orderservice.presentation.controller.impl;

import com.example.orderservice.application.service.OrderService;
import com.example.orderservice.domain.order.OrderStatus;
import com.example.orderservice.presentation.controller.OrderControllerApi;
import com.example.orderservice.presentation.dto.order.OrderRequestDto;
import com.example.orderservice.presentation.dto.order.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderControllerApi {

    private final OrderService orderService;

    @Override
    public ResponseEntity<OrderResponseDto> createOrder(OrderRequestDto orderRequest) {
        OrderResponseDto createdOrder = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(createdOrder);
    }

    @Override
    public ResponseEntity<OrderResponseDto> updateOrder(Long orderId, OrderRequestDto orderRequest) {
        OrderResponseDto updatedOrder = orderService.updateOrder(orderId, orderRequest);
        return ResponseEntity.ok(updatedOrder);
    }

    @Override
    public ResponseEntity<List<OrderResponseDto>> getOrders(OrderStatus status, Double minPrice, Double maxPrice) {
        List<OrderResponseDto> orders = orderService.getOrders(status, minPrice, maxPrice);
        return ResponseEntity.ok(orders);
    }

    @Override
    public ResponseEntity<OrderResponseDto> getOrderById(Long orderId) {
        OrderResponseDto order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @Override
    public ResponseEntity<Void> deleteOrder(Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
