package com.example.orderservice.application.service.impl;

import com.example.orderservice.application.event.OrderStatusChangedEvent;
import com.example.orderservice.application.service.OrderService;
import com.example.orderservice.domain.order.Order;
import com.example.orderservice.domain.order.OrderStatus;
import com.example.orderservice.infrastructure.audit.anatation.Auditable;
import com.example.orderservice.infrastructure.config.security.UserContextHelper;
import com.example.orderservice.infrastructure.mapper.OrderMapper;
import com.example.orderservice.infrastructure.repository.OrderRepository;
import com.example.orderservice.presentation.dto.order.OrderRequestDto;
import com.example.orderservice.presentation.dto.order.OrderResponseDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final UserContextHelper userContextHelper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @Auditable(action = "Create order")
    @CacheEvict(value = "orders", allEntries = true)
    public OrderResponseDto createOrder(OrderRequestDto orderRequest) {
        return mapper.toDto(repository.save(mapper.toEntity(orderRequest)));
    }

    @Override
    @Transactional
    @Auditable(action = "Update order")
    @CachePut(value = "orders", key = "#orderId")
    public OrderResponseDto updateOrder(Long orderId, OrderRequestDto orderRequest) {
        String currentUserEmail = userContextHelper.getCurrentUserEmail();
        Order existingOrder = repository.findByIdAndNotDeleted(orderId)
                .filter(order -> order.getCustomerName().equals(currentUserEmail) || userContextHelper.isAdmin())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with ID %d not found or access denied", orderId)
                ));

        Order updatedOrder = mapper.toEntity(orderRequest);
        updatedOrder.setId(existingOrder.getId());

        Order savedOrder = repository.save(updatedOrder);

        // Publish event after saving
        // In the future, this event can be sent to a message broker (e.g., Kafka, RabbitMQ)
        // to notify other services or components about the order status change.
        if (!existingOrder.getStatus().equals(savedOrder.getStatus())) {
            eventPublisher.publishEvent(new OrderStatusChangedEvent(
                    orderId,
                    existingOrder.getStatus(),
                    savedOrder.getStatus()
            ));
        }

        return mapper.toDto(savedOrder);
    }

    @Override
    @Auditable(action = "Get order by id")
    @Transactional
    @Cacheable(value = "orders", key = "#orderId")
    public OrderResponseDto getOrderById(Long orderId) {
        String currentUserEmail = userContextHelper.getCurrentUserEmail();
        Order order = repository.findByIdAndNotDeleted(orderId)
                .filter(o -> o.getCustomerName().equals(currentUserEmail) || userContextHelper.isAdmin())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with ID %d not found or access denied", orderId)
                ));
        return mapper.toDto(order);
    }

    @Override
    @Transactional
    @Auditable(action = "Get orders")
    @Cacheable(
            value = "orders",
            key = "T(String).format('%s-%s-%s-%s-%s', " +
                    "@userContextHelper.isAdmin() ? 'ADMIN' : 'USER', " +
                    "#status ?: 'ALL', " +
                    "#minPrice ?: 'NONE', " +
                    "#maxPrice ?: 'NONE', " +
                    "@userContextHelper.isAdmin() ? 'ALL_USERS' : @userContextHelper.getCurrentUserEmail())",
            unless = "#result.isEmpty()"
    )
    public List<OrderResponseDto> getOrders(OrderStatus status, Double minPrice, Double maxPrice) {
        if (userContextHelper.isAdmin()) {
            return repository.findOrdersByFilters(status, minPrice, maxPrice).stream()
                    .map(mapper::toDto)
                    .toList();
        } else {
            String currentUserEmail = userContextHelper.getCurrentUserEmail();
            return repository.findOrdersByFiltersForUser(currentUserEmail, status, minPrice, maxPrice).stream()
                    .map(mapper::toDto)
                    .toList();
        }
    }

    @Override
    @Transactional
    @Auditable(action = "Delete order")
    @CacheEvict(value = "orders", key = "#orderId")
    public void deleteOrder(Long orderId) {
        String currentUserEmail = userContextHelper.getCurrentUserEmail();
        Order order = repository.findByIdAndNotDeleted(orderId)
                .filter(o -> o.getCustomerName().equals(currentUserEmail) || userContextHelper.isAdmin())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with ID %d not found or access denied", orderId)
                ));
        order.setIsDeleted(true);
        order.getProducts().forEach(p -> p.setIsDeleted(true));

        repository.save(order);
    }
}
