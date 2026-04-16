package com.workfarm.timemanagement.service;

public interface PasswordHasher {
    String hash(String rawPassword);
}
