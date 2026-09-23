package com.jonis.auth.dto;

public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds) {
}
