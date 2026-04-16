package com.workfarm.timemanagement.dto.auth;

import java.util.UUID;

import com.workfarm.timemanagement.entity.UserRole;

public record RegisterResponseDto(
    UUID userId,
    String username,
    String email,
    UserRole role,
    Integer pomodoroMinutes,
    Integer breakMinutes,
    String message
) {
}
