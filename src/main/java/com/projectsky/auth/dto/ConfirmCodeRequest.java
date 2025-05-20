package com.projectsky.auth.dto;

public record ConfirmCodeRequest(
        String email,
        String code
) {
}
