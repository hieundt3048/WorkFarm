package com.workfarm.timemanagement.service;

import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class PlainTextPasswordVerifier implements PasswordVerifier {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    // Ưu tiên so khớp BCrypt, fallback plaintext cho dữ liệu cũ chưa migrate.
    public boolean matches(String rawPassword, String storedPasswordHash) {
        if (storedPasswordHash == null) {
            return false;
        }

        if (storedPasswordHash.startsWith("$2a$")
            || storedPasswordHash.startsWith("$2b$")
            || storedPasswordHash.startsWith("$2y$")) {
            return encoder.matches(rawPassword, storedPasswordHash);
        }

        return storedPasswordHash.equals(rawPassword);
    }
}
