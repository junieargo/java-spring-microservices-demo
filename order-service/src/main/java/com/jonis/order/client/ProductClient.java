package com.jonis.order.client;

import com.jonis.order.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * "product-service" is resolved through Eureka -- this never hardcodes
 * a host or port. Points at product-service's plain-JSON internal
 * endpoint rather than its Data REST HAL endpoint; see
 * ProductInternalController on the product-service side for why.
 *
 * No custom ErrorDecoder here on purpose: Feign's default decoder
 * already turns a 404 response into feign.FeignException.NotFound,
 * which OrderService catches directly and re-throws as the domain
 * specific ProductNotFoundException (with the correct product id
 * attached, which a generic decoder at this layer would not have).
 */
@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/products/{id}/details")
    ProductDto getProductDetails(@PathVariable("id") Long id);
}
