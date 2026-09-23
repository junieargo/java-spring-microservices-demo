package com.jonis.order.dto;

import java.math.BigDecimal;

/**
 * order-service's own view of a product -- deliberately independent
 * from product-service's internal Product entity. Each service owns
 * its contract; this record only needs the fields order-service
 * actually uses.
 */
public record ProductDto(Long id, String name, BigDecimal price, Integer stock) {
}
