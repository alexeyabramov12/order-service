package com.example.orderservice.presentation.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for representing a product in an order request")
public class ProductRequestDto {

    @Schema(description = "The unique ID of the product",
            example = "1")
    @Min(value = 0, message = "Id must be at least 0")
    private Long id;

    @Schema(description = "The name of the product",
            example = "Laptop",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Product name must not be blank")
    private String name;

    @Schema(description = "The price of the product",
            example = "899.99",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Product price must not be null")
    @Min(value = 0, message = "Product price must be at least 0")
    private Double price;

    @Schema(description = "The quantity of the product",
            example = "2",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Product quantity must not be null")
    @Min(value = 1, message = "Product quantity must be at least 1")
    private Integer quantity;
}
