package com.jonis.product.controller;

import com.jonis.product.dto.ProductDetailsDto;
import com.jonis.product.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Plain-JSON read endpoint intended for other services (order-service's
 * Feign client), kept separate from the HAL+JSON endpoints Spring Data
 * REST generates for /api/products. Public GET, same as the rest of
 * the catalog -- see SecurityConfig.
 */
@RestController
@RequestMapping("/api/products")
public class ProductInternalController {

    private final ProductRepository productRepository;

    public ProductInternalController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ProductDetailsDto> getDetails(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(p -> new ProductDetailsDto(p.getId(), p.getName(), p.getPrice(), p.getStock()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
