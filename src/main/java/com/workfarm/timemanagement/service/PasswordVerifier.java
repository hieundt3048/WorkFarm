package com.workfarm.timemanagement.service;

public interface PasswordVerifier {
    boolean matches(String rawPassword, String storedPasswordHash);
}
