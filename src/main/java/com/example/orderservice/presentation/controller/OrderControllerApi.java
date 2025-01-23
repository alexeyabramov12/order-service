package com.example.orderservice.presentation.controller;

import com.example.orderservice.domain.order.OrderStatus;
import com.example.orderservice.presentation.dto.order.OrderRequestDto;
import com.example.orderservice.presentation.dto.order.OrderResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Orders", description = "API for managing orders")
@RequestMapping("/orders")
public interface OrderControllerApi {

    @Operation(summary = "Create a new order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequest);

    @Operation(summary = "Update an existing order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{orderId}")
    ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long orderId,
                                                 @Valid @RequestBody OrderRequestDto orderRequest);

    @Operation(summary = "Get a list of orders with optional filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    ResponseEntity<List<OrderResponseDto>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice);

    @Operation(summary = "Get an order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{orderId}")
    ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long orderId);

    @Operation(summary = "Soft delete an order by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{orderId}")
    ResponseEntity<Void> deleteOrder(@PathVariable Long orderId);
}
