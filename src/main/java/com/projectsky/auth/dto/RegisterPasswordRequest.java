package com.projectsky.auth.dto;

public record RegisterPasswordRequest(
        String email,
        String password
) {
}
