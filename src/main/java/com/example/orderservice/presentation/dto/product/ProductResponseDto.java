package com.example.orderservice.presentation.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for representing a product in an order response")
public class ProductResponseDto {

    @Schema(description = "The unique ID of the product",
            example = "1")
    private Long id;

    @Schema(description = "The name of the product",
            example = "Laptop")
    private String name;

    @Schema(description = "The price of the product",
            example = "899.99")
    private Double price;

    @Schema(description = "The quantity of the product",
            example = "2")
    private Integer quantity;
}
