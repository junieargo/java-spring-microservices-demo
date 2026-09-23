package com.jonis.product.repository;

import com.jonis.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * Spring Data REST exposes this as a full CRUD API at /api/products
 * (GET, POST, PUT, PATCH, DELETE) with zero controller code. The
 * derived query below is auto-exposed at /api/products/search/byName.
 */
@RepositoryRestResource(path = "products", collectionResourceRel = "products")
@CrossOrigin
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);
}
