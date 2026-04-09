package com.workfarm.timemanagement.dto.focussession;

import java.util.UUID;

import com.workfarm.timemanagement.entity.SessionType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StartFocusSessionRequestDto(
    @NotNull UUID userId,
    @NotNull UUID taskId,
    @NotNull SessionType sessionType,
    @NotNull @Min(1) Integer durationMinutes
) {
}
