package com.jonis.order.service;

import com.jonis.order.client.ProductClient;
import com.jonis.order.dto.PlaceOrderRequest;
import com.jonis.order.dto.ProductDto;
import com.jonis.order.entity.Order;
import com.jonis.order.exception.InsufficientStockException;
import com.jonis.order.exception.ProductNotFoundException;
import com.jonis.order.repository.OrderRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    public Order placeOrder(PlaceOrderRequest request) {
        ProductDto product = fetchProduct(request.productId());

        if (product.stock() < request.quantity()) {
            throw new InsufficientStockException(request.productId(), request.quantity(), product.stock());
        }

        Order order = new Order(
                null,
                product.id(),
                product.name(),
                request.quantity(),
                product.price(),
                product.price().multiply(java.math.BigDecimal.valueOf(request.quantity())),
                LocalDateTime.now()
        );

        return orderRepository.save(order);

        // Known simplification: this does not decrement product-service's
        // stock count. Doing that correctly across two services means
        // either a distributed transaction or a saga/compensation flow
        // (e.g. reserve stock, confirm on order success, release on
        // failure) -- worth raising proactively in a review rather than
        // silently pretending consistency is solved here.
    }

    private ProductDto fetchProduct(Long productId) {
        try {
            return productClient.getProductDetails(productId);
        } catch (FeignException.NotFound ex) {
            throw new ProductNotFoundException(productId);
        }
    }
}
