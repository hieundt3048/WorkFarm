package com.workfarm.timemanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.workfarm.timemanagement.dto.focussession.FocusSessionResponseDto;
import com.workfarm.timemanagement.dto.focussession.FocusSessionTimeoutNotificationDto;
import com.workfarm.timemanagement.dto.focussession.StartFocusSessionRequestDto;
import com.workfarm.timemanagement.service.FocusSessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/focus-sessions")
@RequiredArgsConstructor
public class FocusSessionController {

    private final FocusSessionService focusSessionService;

    @PostMapping("/start")
    @ResponseStatus(HttpStatus.CREATED)
    public FocusSessionResponseDto startSession(@Valid @RequestBody StartFocusSessionRequestDto request) {
        return focusSessionService.startSession(request);
    }

    @PostMapping("/{sessionId}/pause")
    public FocusSessionResponseDto pauseSession(@PathVariable UUID sessionId) {
        return focusSessionService.pauseSession(sessionId);
    }

    @PostMapping("/{sessionId}/cancel")
    public FocusSessionResponseDto cancelSession(@PathVariable UUID sessionId) {
        return focusSessionService.cancelSession(sessionId);
    }

    @GetMapping("/users/{userId}/history")
    public List<FocusSessionResponseDto> getSessionHistory(@PathVariable UUID userId) {
        return focusSessionService.getSessionHistory(userId);
    }

    @GetMapping("/users/{userId}/timeout-notifications")
    public List<FocusSessionTimeoutNotificationDto> getTimeoutNotifications(@PathVariable UUID userId) {
        return focusSessionService.getTimeoutNotifications(userId);
    }
}
