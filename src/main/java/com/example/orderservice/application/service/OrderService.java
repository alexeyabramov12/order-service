package com.example.orderservice.application.service;

import com.example.orderservice.domain.order.OrderStatus;
import com.example.orderservice.presentation.dto.order.OrderRequestDto;
import com.example.orderservice.presentation.dto.order.OrderResponseDto;

import java.util.List;

/**
 * Service interface for managing Orders in the system.
 * Provides methods for creating, updating, retrieving, and deleting orders.
 */
public interface OrderService {

    /**
     * Creates a new order based on the provided {@link OrderRequestDto}.
     *
     * @param orderRequest the DTO containing the details of the order to create.
     * @return an {@link OrderResponseDto} representing the created order.
     */
    OrderResponseDto createOrder(OrderRequestDto orderRequest);

    /**
     * Updates an existing order identified by its ID.
     *
     * @param orderId      the ID of the order to update.
     * @param orderRequest the DTO containing the updated details of the order.
     * @return an {@link OrderResponseDto} representing the updated order.
     */
    OrderResponseDto updateOrder(Long orderId, OrderRequestDto orderRequest);

    /**
     * Retrieves a list of orders filtered by the provided criteria.
     *
     * @param status   the status of the orders to retrieve (e.g., PENDING, CONFIRMED, CANCELLED).
     *                 Can be null to ignore this filter.
     * @param minPrice the minimum total price of the orders to retrieve.
     *                 Can be null to ignore this filter.
     * @param maxPrice the maximum total price of the orders to retrieve.
     *                 Can be null to ignore this filter.
     * @return a list of {@link OrderResponseDto} representing the orders matching the criteria.
     */
    List<OrderResponseDto> getOrders(OrderStatus status, Double minPrice, Double maxPrice);

    /**
     * Retrieves the details of a specific order by its ID.
     *
     * @param orderId the ID of the order to retrieve.
     * @return an {@link OrderResponseDto} representing the order.
     * @throws jakarta.persistence.EntityNotFoundException if no order is found with the given ID.
     */
    OrderResponseDto getOrderById(Long orderId);

    /**
     * Soft deletes an order by marking it as deleted.
     * The order remains in the database but is excluded from normal operations.
     *
     * @param orderId the ID of the order to delete.
     * @throws jakarta.persistence.EntityNotFoundException if no order is found with the given ID.
     */
    void deleteOrder(Long orderId);
}
