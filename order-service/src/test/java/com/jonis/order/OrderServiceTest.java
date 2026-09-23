package com.jonis.order;

import com.jonis.order.client.ProductClient;
import com.jonis.order.dto.PlaceOrderRequest;
import com.jonis.order.dto.ProductDto;
import com.jonis.order.entity.Order;
import com.jonis.order.exception.InsufficientStockException;
import com.jonis.order.repository.OrderRepository;
import com.jonis.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pure unit test: the Feign client and repository are both mocked, so
 * this exercises OrderService's pricing/validation logic in isolation
 * without needing product-service, Eureka, or a real database running.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void placesOrderAndComputesTotalFromLiveProductPrice() {
        ProductDto product = new ProductDto(1L, "Mechanical Keyboard", new BigDecimal("75.00"), 25);
        when(productClient.getProductDetails(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order order = orderService.placeOrder(new PlaceOrderRequest(1L, 3));

        assertThat(order.getProductId()).isEqualTo(1L);
        assertThat(order.getProductName()).isEqualTo("Mechanical Keyboard");
        assertThat(order.getQuantity()).isEqualTo(3);
        assertThat(order.getUnitPrice()).isEqualByComparingTo("75.00");
        assertThat(order.getTotalPrice()).isEqualByComparingTo("225.00");
    }

    @Test
    void rejectsOrderWhenRequestedQuantityExceedsStock() {
        ProductDto product = new ProductDto(2L, "27-inch Monitor", new BigDecimal("249.99"), 2);
        when(productClient.getProductDetails(2L)).thenReturn(product);

        assertThatThrownBy(() -> orderService.placeOrder(new PlaceOrderRequest(2L, 5)))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("requested 5")
                .hasMessageContaining("available 2");
    }
}
