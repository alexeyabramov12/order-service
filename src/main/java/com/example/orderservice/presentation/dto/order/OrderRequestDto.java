package com.example.orderservice.presentation.dto.order;

import com.example.orderservice.presentation.dto.product.ProductRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating or updating an order")
public class OrderRequestDto {

    @Schema(description = "The customer's email address",
            example = "customer@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Customer name must not be blank")
    @Email(message = "Customer name must be a valid email")
    private String customerName;

    @Schema(description = "The status of the order",
            example = "PENDING",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Status must not be blank")
    private String status;

    @Schema(description = "The total price of the order",
            example = "199.99",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Total price must not be null")
    private Double totalPrice;

    @Schema(description = "The list of products in the order",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Products list must not be null")
    @Size(min = 1, message = "Order must contain at least one product")
    private List<ProductRequestDto> products;
}
