package com.jonis.product.config;

import com.jonis.product.entity.Product;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;

/**
 * By default Spring Data REST strips entity IDs from its JSON responses
 * (it expects clients to navigate by HATEOAS links instead). That is
 * unhelpful for a demo/test API and for order-service's Feign client,
 * which needs the numeric id directly, so we expose it explicitly.
 */
@Configuration
public class RestConfig implements RepositoryRestConfigurer {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, org.springframework.web.servlet.config.annotation.CorsRegistry cors) {
        config.exposeIdsFor(Product.class);
    }
}
