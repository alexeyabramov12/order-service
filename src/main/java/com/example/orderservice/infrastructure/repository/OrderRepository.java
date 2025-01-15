package com.example.orderservice.infrastructure.repository;

import com.example.orderservice.domain.order.Order;
import com.example.orderservice.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link Order} entities in the database.
 * Extends {@link JpaRepository} to provide basic CRUD operations and custom queries.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds a list of orders based on the specified filters.
     * The filters include order status, minimum price, and maximum price.
     * Orders marked as deleted are excluded from the results.
     *
     * @param status   the status of the orders to filter by (e.g., PENDING, CONFIRMED, CANCELLED).
     *                 If {@code null}, the filter is ignored.
     * @param minPrice the minimum total price of the orders to filter by.
     *                 If {@code null}, the filter is ignored.
     * @param maxPrice the maximum total price of the orders to filter by.
     *                 If {@code null}, the filter is ignored.
     * @return a list of orders matching the specified filters, excluding deleted orders.
     */
    @Query("SELECT o FROM Order o WHERE " +
            "(o.status = :status OR :status IS NULL) AND " +
            "(o.totalPrice >= :minPrice OR :minPrice IS NULL) AND " +
            "(o.totalPrice <= :maxPrice OR :maxPrice IS NULL) AND " +
            "o.isDeleted = false")
    List<Order> findOrdersByFilters(OrderStatus status, Double minPrice, Double maxPrice);

    /**
     * Finds an order by its ID, ensuring the order is not marked as deleted.
     *
     * @param id the ID of the order to retrieve.
     * @return an {@link Optional} containing the order if found and not marked as deleted, otherwise empty.
     */
    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.isDeleted = false")
    Optional<Order> findByIdAndNotDeleted(Long id);
}
