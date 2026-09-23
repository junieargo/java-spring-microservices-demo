package com.jonis.auth.controller;

import com.jonis.auth.dto.LoginRequest;
import com.jonis.auth.dto.TokenResponse;
import com.jonis.auth.model.AppUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Minimal OAuth2-style token issuer.
 *
 * This is NOT a full Spring Authorization Server / OAuth2 flow (no
 * client_id, no grant types, no refresh tokens). It exists to satisfy
 * the study case's "API Security with OAuth2" line by giving
 * product-service and order-service real signed JWTs to validate as
 * resource servers, using the same jwt.secret pulled from
 * config-service. If this needed to be production-grade, the honest
 * next step is swapping this controller for Spring Authorization
 * Server or an external IdP (Keycloak, Auth0) -- the resource-server
 * side (product-service / order-service) would not need to change.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final List<AppUser> USERS = List.of(
            new AppUser("admin", "admin123", List.of("ADMIN", "USER")),
            new AppUser("jonis", "jonis123", List.of("USER"))
    );

    private final String jwtSecret;

    public AuthController(@Value("${jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return USERS.stream()
                .filter(u -> u.username().equals(request.username()) && u.password().equals(request.password()))
                .findFirst()
                .map(this::issueToken)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "invalid username or password")));
    }

    private TokenResponse issueToken(AppUser user) {
        long expirySeconds = 3600;
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Instant now = Instant.now();

        String token = Jwts.builder()
                .subject(user.username())
                .claim("roles", user.roles())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirySeconds)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        return new TokenResponse(token, "Bearer", expirySeconds);
    }
}
