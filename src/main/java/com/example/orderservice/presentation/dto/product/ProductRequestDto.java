package com.example.orderservice.presentation.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for creating or updating a product in an order.
 * Contains validation constraints to ensure valid input data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    /**
     * The name of the product.
     * Must not be blank.
     */
    @NotBlank(message = "Product name must not be blank")
    private String name;

    /**
     * The price of the product.
     * Must not be null and must be greater than or equal to 0.01.
     */
    @NotNull(message = "Product price must not be null")
    @Min(value = 0, message = "Product price must be at least 0")
    private Double price;

    /**
     * The quantity of the product.
     * Must not be null and must be greater than or equal to 1.
     */
    @NotNull(message = "Product quantity must not be null")
    @Min(value = 1, message = "Product quantity must be at least 1")
    private Integer quantity;
}
