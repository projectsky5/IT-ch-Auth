package com.projectsky.auth.service;

public interface EmailService {
    void sendConfirmationCode(String to, String code);
}
