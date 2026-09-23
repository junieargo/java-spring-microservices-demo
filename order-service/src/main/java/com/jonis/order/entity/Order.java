package com.jonis.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * unitPrice and productName are captured at order time (rather than
 * joined live from product-service on every read) so that historical
 * orders remain accurate even if the product's price or name changes
 * later -- a standard pattern for order line items in any e-commerce
 * domain, and a natural place to bring up eventual consistency if
 * asked about it.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    private LocalDateTime orderDate;
}
