package com.projectsky.auth.exception;

public class UserNotConfirmedException extends RuntimeException {
    public UserNotConfirmedException(String message) {
        super(message);
    }
}
