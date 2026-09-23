package com.jonis.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Spring Cloud Gateway: single public entry point for the whole system.
 * Routes are resolved against the Eureka registry (lb://SERVICE-NAME)
 * so this service never needs to know the physical host/port of the
 * services it fronts.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
