package com.workfarm.timemanagement.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

@Component
public class RegexEmailFormatValidator implements EmailFormatValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Override
    public boolean isValid(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
