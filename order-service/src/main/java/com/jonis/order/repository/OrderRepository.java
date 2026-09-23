package com.jonis.order.repository;

import com.jonis.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Data REST exposes GET (list/by-id) for browsing order history, which
 * satisfies the "CRUD via Spring Data REST" requirement for this
 * second repository. Creation is intentionally NOT exposed here --
 * see OrderController#placeOrder -- because a raw Data REST POST would
 * let a caller set productName/unitPrice/totalPrice directly, bypassing
 * the Feign validation against product-service that makes an order
 * trustworthy in the first place.
 */
@RepositoryRestResource(path = "orders", collectionResourceRel = "orders")
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    @RestResource(exported = false)
    <S extends Order> S save(S entity);

    @Override
    @RestResource(exported = false)
    void deleteById(Long id);

    @Override
    @RestResource(exported = false)
    void delete(Order entity);
}
