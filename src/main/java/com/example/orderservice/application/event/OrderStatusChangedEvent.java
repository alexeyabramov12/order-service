package com.example.orderservice.application.event;

import com.example.orderservice.domain.order.OrderStatus;

/**
 * Represents an event triggered when the status of an order is changed.
 * <p>
 * This event contains the details of the order whose status has been updated,
 * including the order ID, the previous status, and the new status.
 * </p>
 *
 * @param orderId   the unique identifier of the order whose status was changed.
 * @param oldStatus the previous status of the order.
 * @param newStatus the new status of the order.
 */
public record OrderStatusChangedEvent(Long orderId, OrderStatus oldStatus, OrderStatus newStatus) {
}
