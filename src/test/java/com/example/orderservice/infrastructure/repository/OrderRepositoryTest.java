package com.example.orderservice.infrastructure.repository;

import com.example.orderservice.domain.order.Order;
import com.example.orderservice.domain.order.OrderStatus;
import jakarta.transaction.Transactional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static com.example.orderservice.util.TestContainersUtil.configurePostgres;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        configurePostgres(registry);
    }

    @Autowired
    private OrderRepository repository;

    private Order activeOrder;
    private Order deletedOrder;
    private final SoftAssertions softly = new SoftAssertions();


    @BeforeEach
    void init() {
        activeOrder = new Order(
                "John Doe",
                OrderStatus.PENDING,
                100.0,
                List.of());
        activeOrder.setIsDeleted(false);

        deletedOrder = new Order(
                "Jane Doe",
                OrderStatus.PENDING,
                200.0,
                List.of());
        deletedOrder.setIsDeleted(true);

        activeOrder = repository.save(activeOrder);
        deletedOrder = repository.save(deletedOrder);
    }

    @AfterEach
    void cleanup() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should find active orders by filters")
    void findOrdersByFilters_ValidFilters_ReturnsActiveOrders() {
        List<Order> result = repository.findOrdersByFilters(OrderStatus.PENDING, null, null);

        softly.assertThat(result).isNotNull();
        softly.assertThat(result.size()).isEqualTo(1);
        softly.assertThat(result.getFirst().getCustomerName()).isEqualTo("John Doe");
        softly.assertThat(result.getFirst().getStatus()).isEqualTo(OrderStatus.PENDING);
        softly.assertThat(result.getFirst().getTotalPrice()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Should not return deleted orders")
    void findOrdersByFilters_DeletedOrdersExcluded_ReturnsOnlyActiveOrders() {
        List<Order> result = repository.findOrdersByFilters(null, null, null);

        softly.assertThat(result).isNotNull();
        softly.assertThat(result.size()).isEqualTo(1);
        softly.assertThat(result.getFirst().getCustomerName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should find orders by customer name and filters")
    void findOrdersByFiltersForUser_ValidFilters_ReturnsCustomerOrders() {
        List<Order> result = repository.findOrdersByFiltersForUser("John Doe", null, null, null);

        softly.assertThat(result).isNotNull();
        softly.assertThat(result.size()).isEqualTo(1);
        softly.assertThat(result.getFirst().getCustomerName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should find an active order by ID")
    void findByIdAndNotDeleted_ActiveOrder_ReturnsOrder() {
        Optional<Order> result = repository.findByIdAndNotDeleted(activeOrder.getId());

        softly. assertThat(result).isPresent();
        softly.assertThat(result.get().getCustomerName()).isEqualTo("John Doe");
        softly. assertThat(result.get().getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("Should not find a deleted order by ID")
    void findByIdAndNotDeleted_DeletedOrder_ReturnsEmpty() {
        Optional<Order> result = repository.findByIdAndNotDeleted(deletedOrder.getId());

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Should delete all orders by customer name and filters")
    void deleteByCustomerNameAndFilters_DeletesOrders() {
        repository.deleteAll();

        List<Order> result = repository.findAll();
        assertThat(result).isEmpty();
    }
}
