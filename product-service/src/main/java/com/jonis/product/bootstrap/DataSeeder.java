package com.jonis.product.bootstrap;

import com.jonis.product.entity.Product;
import com.jonis.product.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataSeeder(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            productRepository.save(new Product(null, "Mechanical Keyboard", "Hot-swappable, brown switches", new BigDecimal("75.00"), 25));
            productRepository.save(new Product(null, "27-inch Monitor", "1440p IPS panel, 144Hz", new BigDecimal("249.99"), 10));
            productRepository.save(new Product(null, "USB-C Dock", "10-port docking station", new BigDecimal("89.50"), 40));
        }
    }
}
