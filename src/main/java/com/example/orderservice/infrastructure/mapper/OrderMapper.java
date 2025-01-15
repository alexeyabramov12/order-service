package com.example.orderservice.infrastructure.mapper;

import com.example.orderservice.domain.order.Order;
import com.example.orderservice.domain.product.Product;
import com.example.orderservice.presentation.dto.order.OrderRequestDto;
import com.example.orderservice.presentation.dto.order.OrderResponseDto;
import com.example.orderservice.presentation.dto.product.ProductRequestDto;
import com.example.orderservice.presentation.dto.product.ProductResponseDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * Mapper interface for converting between domain entities and DTOs related to Orders and Products.
 * This interface uses MapStruct for automatic generation of mapping code.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Converts an {@link OrderRequestDto} to an {@link Order} entity.
     * Ignores the {@code id} field and sets the {@code isDeleted} field to {@code false}.
     *
     * @param orderRequestDto the {@link OrderRequestDto} to convert.
     * @return the converted {@link Order} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    Order toEntity(OrderRequestDto orderRequestDto);

    /**
     * Converts an {@link Order} entity to an {@link OrderResponseDto}.
     *
     * @param order the {@link Order} entity to convert.
     * @return the converted {@link OrderResponseDto}.
     */
    OrderResponseDto toDto(Order order);

    /**
     * Converts a {@link ProductRequestDto} to a {@link Product} entity.
     * Ignores the {@code id} field and sets the {@code isDeleted} field to {@code false}.
     *
     * @param productRequestDto the {@link ProductRequestDto} to convert.
     * @return the converted {@link Product} entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    Product toEntity(ProductRequestDto productRequestDto);

    /**
     * Converts a {@link Product} entity to a {@link ProductResponseDto}.
     *
     * @param product the {@link Product} entity to convert.
     * @return the converted {@link ProductResponseDto}.
     */
    ProductResponseDto toDto(Product product);

    /**
     * Links the list of {@link Product} entities to the parent {@link Order} entity.
     * This ensures that each product in the list has a reference to the parent order.
     *
     * @param order the {@link Order} entity to which the products are linked.
     */
    @AfterMapping
    default void linkProducts(@MappingTarget Order order) {
        if (order.getProducts() != null) {
            order.getProducts().forEach(product -> product.setOrder(order));
        }
    }
}
