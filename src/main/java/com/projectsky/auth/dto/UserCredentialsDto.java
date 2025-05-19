package com.projectsky.auth.dto;

public record UserCredentialsDto(
        String email,
        String password
) {
}
