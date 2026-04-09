package com.workfarm.timemanagement.dto.focussession;

import java.time.LocalDateTime;
import java.util.UUID;

public record FocusSessionTimeoutNotificationDto(
    UUID sessionId,
    UUID taskId,
    String taskTitle,
    LocalDateTime endTime,
    String message
) {
}
