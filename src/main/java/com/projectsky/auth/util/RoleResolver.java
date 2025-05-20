package com.projectsky.auth.util;

import org.springframework.stereotype.Component;

@Component
public class RoleResolver {

    public String resolveRoleFromEmail(String email) {
        if (email.endsWith("@edu.hse.ru")) {
            return "STUDENT";
        } else if (email.endsWith("@hse.ru")) {
            return "TEACHER";
        } else {
            throw new IllegalArgumentException("Unsupported domain in email: " + email);
        }
    }
}
