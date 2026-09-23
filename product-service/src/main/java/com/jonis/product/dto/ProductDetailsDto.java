package com.jonis.product.dto;

import java.math.BigDecimal;

public record ProductDetailsDto(Long id, String name, BigDecimal price, Integer stock) {
}
