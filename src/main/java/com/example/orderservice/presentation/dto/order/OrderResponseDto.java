package com.example.orderservice.presentation.dto.order;

import com.example.orderservice.presentation.dto.product.ProductResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for representing an order response")
public class OrderResponseDto {

    @Schema(description = "The unique ID of the order",
            example = "1")
    private Long id;

    @Schema(description = "The customer's email address",
            example = "customer@example.com")
    private String customerName;

    @Schema(description = "The status of the order",
            example = "PENDING")
    private String status;

    @Schema(description = "The total price of the order",
            example = "199.99")
    private BigDecimal totalPrice;

    @Schema(description = "The list of products in the order")
    private List<ProductResponseDto> products;
}
