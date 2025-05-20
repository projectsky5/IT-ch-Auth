package com.projectsky.auth.dto;

public record UserCreateRequest(
        String email,
        String fullName,
        String role
) {
}
