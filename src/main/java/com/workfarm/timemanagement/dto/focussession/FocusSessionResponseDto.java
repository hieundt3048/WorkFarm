package com.workfarm.timemanagement.dto.focussession;

import com.workfarm.timemanagement.entity.SessionType;

import java.time.LocalDateTime;
import java.util.UUID;

public record FocusSessionResponseDto(
    UUID id,
    UUID userId,
    UUID taskId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    Integer actualDuration,
    SessionType sessionType,
    Boolean isCompleted
) {
}
