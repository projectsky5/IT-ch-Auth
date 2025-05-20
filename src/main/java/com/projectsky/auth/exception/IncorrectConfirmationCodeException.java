package com.projectsky.auth.exception;

public class IncorrectConfirmationCodeException extends RuntimeException {
    public IncorrectConfirmationCodeException(String message) {
        super(message);
    }
}
