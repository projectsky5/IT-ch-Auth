package com.projectsky.auth.dto;

public record ErrorResponse(
        int status,
        String error,
        String message
) {
}
