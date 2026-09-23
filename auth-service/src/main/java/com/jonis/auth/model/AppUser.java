package com.jonis.auth.model;

import java.util.List;

/**
 * Deliberately in-memory: this is a demo auth server whose only job is
 * to prove the OAuth2 resource-server wiring in product-service and
 * order-service. A real system would back this with a user store and
 * a proper password hash comparison (BCrypt), not a hardcoded list.
 */
public record AppUser(String username, String password, List<String> roles) {
}
