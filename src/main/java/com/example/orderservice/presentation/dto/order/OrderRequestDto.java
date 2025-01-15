package com.example.orderservice.presentation.dto.order;

import com.example.orderservice.presentation.dto.product.ProductRequestDto;
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
public class OrderRequestDto {
    private String customerName;
    private String status;
    private BigDecimal totalPrice;
    private List<ProductRequestDto> products;
}
