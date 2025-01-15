package com.example.orderservice.application.service.impl;

import com.example.orderservice.application.service.OrderService;
import com.example.orderservice.domain.order.Order;
import com.example.orderservice.domain.order.OrderStatus;
import com.example.orderservice.infrastructure.mapper.OrderMapper;
import com.example.orderservice.infrastructure.repository.OrderRepository;
import com.example.orderservice.presentation.dto.order.OrderRequestDto;
import com.example.orderservice.presentation.dto.order.OrderResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequest) {
        return mapper.toDto(repository.save(mapper.toEntity(orderRequest)));
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrder(Long orderId, OrderRequestDto orderRequest) {
        Order existingOrder = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with ID %d not found", orderId)
                ));

        Order updatedOrder = mapper.toEntity(orderRequest);
        updatedOrder.setId(existingOrder.getId());

        Order savedOrder = repository.save(updatedOrder);
        return mapper.toDto(savedOrder);
    }


    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrders(OrderStatus status, Double minPrice, Double maxPrice) {
        return repository.findOrdersByFilters(status, minPrice, maxPrice).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = repository.findByIdAndNotDeleted(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with ID %d not found", orderId)
                ));

        return mapper.toDto(order);
    }

    @Override
    @Transactional()
    public void deleteOrder(Long orderId) {
        Order order = repository.findByIdAndNotDeleted(orderId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with ID %d not found", orderId)
                ));

        order.setIsDeleted(true);
        order.getProducts().forEach(p -> p.setIsDeleted(true));

        repository.save(order);
    }
}
