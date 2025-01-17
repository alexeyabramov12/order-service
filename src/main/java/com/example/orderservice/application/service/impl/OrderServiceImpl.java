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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        String currentUserEmail = getCurrentUserEmail();
        Order existingOrder = repository.findByIdAndNotDeleted(orderId)
                .filter(order -> order.getCustomerName().equals(currentUserEmail) || isAdmin())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with ID %d not found or access denied", orderId)
                ));

        Order updatedOrder = mapper.toEntity(orderRequest);
        updatedOrder.setId(existingOrder.getId());

        Order savedOrder = repository.save(updatedOrder);
        return mapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrders(OrderStatus status, Double minPrice, Double maxPrice) {
        if (isAdmin()) {
            return repository.findOrdersByFilters(status, minPrice, maxPrice).stream()
                    .map(mapper::toDto)
                    .toList();
        } else {
            String currentUserEmail = getCurrentUserEmail();
            return repository.findOrdersByFiltersForUser(currentUserEmail, status, minPrice, maxPrice).stream()
                    .map(mapper::toDto)
                    .toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        String currentUserEmail = getCurrentUserEmail();
        Order order = repository.findByIdAndNotDeleted(orderId)
                .filter(o -> o.getCustomerName().equals(currentUserEmail) || isAdmin())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with ID %d not found or access denied", orderId)
                ));
        return mapper.toDto(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        String currentUserEmail = getCurrentUserEmail();
        Order order = repository.findByIdAndNotDeleted(orderId)
                .filter(o -> o.getCustomerName().equals(currentUserEmail) || isAdmin())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Order with ID %d not found or access denied", orderId)
                ));
        order.setIsDeleted(true);
        order.getProducts().forEach(p -> p.setIsDeleted(true));

        repository.save(order);
    }

    /**
     * Checks if the currently authenticated user has the "Admin" role.
     * <p>
     * This method retrieves the authentication details from the {@link SecurityContextHolder}
     * and checks if the user's authorities contain the "ROLE_Admin".
     *
     * @return {@code true} if the current user has the "Admin" role, {@code false} otherwise.
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_Admin"));
    }

    /**
     * Retrieves the email of the currently authenticated user.
     * <p>
     * This method fetches the authentication details from the {@link SecurityContextHolder}
     * and extracts the username (email) from the {@link UserDetails}.
     *
     * @return the email of the currently authenticated user.
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
