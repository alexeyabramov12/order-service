package com.example.orderservice.presentation.dto.order;

import com.example.orderservice.presentation.dto.product.ProductRequestDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for creating or updating an order.
 * Contains validation constraints to ensure valid input data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    /**
     * The customer's email address.
     * Must be a valid email format and not blank.
     */
    @NotBlank(message = "Customer name must not be blank")
    @Email(message = "Customer name must be a valid email")
    private String customerName;

    /**
     * The status of the order.
     * Must not be blank.
     */
    @NotBlank(message = "Status must not be blank")
    private String status;

    /**
     * The total price of the order.
     * Must not be null and must be a positive value.
     */
    @NotNull(message = "Total price must not be null")
    private BigDecimal totalPrice;

    /**
     * The list of products in the order.
     * Must contain at least one product.
     */
    @NotNull(message = "Products list must not be null")
    @Size(min = 1, message = "Order must contain at least one product")
    private List<ProductRequestDto> products;
}
