package com.projectsky.auth.dto;

public record RegisterInitRequest(
        String email,
        String fullName
) {
}
