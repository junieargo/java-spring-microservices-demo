package com.jonis.product;

import com.jonis.product.entity.Product;
import com.jonis.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @DataJpaTest boots only the JPA slice (in-memory H2, no web layer,
 * no security filter chain) so this runs fast and in isolation.
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void savesAndReloadsProduct() {
        Product saved = productRepository.save(
                new Product(null, "Wireless Mouse", "2.4GHz, silent click", new BigDecimal("29.90"), 100)
        );

        assertThat(saved.getId()).isNotNull();

        Product reloaded = productRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getName()).isEqualTo("Wireless Mouse");
        assertThat(reloaded.getStock()).isEqualTo(100);
    }

    @Test
    void findsByNameCaseInsensitive() {
        productRepository.save(new Product(null, "Gaming Chair", "Ergonomic, lumbar support", new BigDecimal("199.00"), 5));

        List<Product> results = productRepository.findByNameContainingIgnoreCase("gaming");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Gaming Chair");
    }

    @Test
    void returnsEmptyListWhenNoNameMatches() {
        List<Product> results = productRepository.findByNameContainingIgnoreCase("nonexistent-item-xyz");
        assertThat(results).isEmpty();
    }
}
