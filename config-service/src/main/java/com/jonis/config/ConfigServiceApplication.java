package com.jonis.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Centralized configuration server.
 * Uses the "native" profile, meaning it serves config from files on its
 * own classpath (config-repo/) rather than a remote Git repository, so
 * this project has no external dependency to run. In a real deployment
 * this would point spring.cloud.config.server.native.search-locations
 * at a private Git repo instead.
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}
