package com.workfarm.timemanagement.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workfarm.timemanagement.dto.focussession.FocusSessionResponseDto;
import com.workfarm.timemanagement.dto.focussession.FocusSessionTimeoutNotificationDto;
import com.workfarm.timemanagement.dto.focussession.StartFocusSessionRequestDto;
import com.workfarm.timemanagement.entity.FocusSession;
import com.workfarm.timemanagement.entity.Task;
import com.workfarm.timemanagement.repository.FocusSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FocusSessionService {

    private final FocusSessionRepository focusSessionRepository;
    private final UserService userService;
    private final TaskService taskService;

    @Transactional
    // Bắt đầu một phiên làm việc mới cho user và task được chọn.
    // Kết quả: tạo FocusSession đang chạy (isCompleted=false), có start/end theo duration.
    public FocusSessionResponseDto startSession(StartFocusSessionRequestDto request) {
        LocalDateTime now = LocalDateTime.now();
        // Mỗi user chỉ được có một phiên đang chạy tại một thời điểm.
        if (focusSessionRepository.existsByUser_IdAndIsCompletedFalseAndEndTimeAfter(request.userId(), now)) {
            throw new IllegalStateException("User already has an active focus session");
        }

        FocusSession focusSession = FocusSession.builder()
            .user(userService.requireUser(request.userId()))
            .task(taskService.requireTask(request.taskId()))
            .startTime(now)
            .endTime(now.plusMinutes(request.durationMinutes()))
            .actualDuration(request.durationMinutes())
            .sessionType(request.sessionType())
            .isCompleted(false)
            .isNotificationSent(false)
            .build();

        return toResponse(focusSessionRepository.save(focusSession));
    }

    @Transactional(readOnly = true)
    // Lấy lịch sử phiên làm việc của user (mới nhất trước) để hiển thị năng suất.
    public List<FocusSessionResponseDto> getSessionHistory(UUID userId) {
        userService.requireUser(userId);
        return focusSessionRepository.findByUser_IdOrderByStartTimeDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional
    // Tạm dừng phiên đang chạy: chốt thời gian thực tế, cộng vào tổng thời gian tập trung của task.
    public FocusSessionResponseDto pauseSession(UUID sessionId) {
        FocusSession session = requireFocusSession(sessionId);
        ensureSessionIsRunning(session);

        LocalDateTime now = LocalDateTime.now();
        // Khi pause, lưu thời gian thực tế đã tập trung (tối thiểu 1 phút).
        int elapsedMinutes = (int) Math.max(1, ChronoUnit.MINUTES.between(session.getStartTime(), now));

        session.setEndTime(now);
        session.setActualDuration(elapsedMinutes);
        session.setIsCompleted(true);
        session.setIsNotificationSent(true);

        Task task = session.getTask();
        Integer totalFocusTime = task.getTotalFocusTime();
        int currentTotalFocusTime = totalFocusTime == null ? 0 : totalFocusTime;
        task.setTotalFocusTime(currentTotalFocusTime + elapsedMinutes);

        return toResponse(focusSessionRepository.save(session));
    }

    @Transactional
    // Hủy phiên đang chạy: đóng phiên ngay lập tức, không cộng thời gian vào task.
    public FocusSessionResponseDto cancelSession(UUID sessionId) {
        FocusSession session = requireFocusSession(sessionId);
        ensureSessionIsRunning(session);

        // Hủy phiên: kết thúc ngay và không tính thời gian vào năng suất.
        session.setEndTime(LocalDateTime.now());
        session.setActualDuration(0);
        session.setIsCompleted(true);
        session.setIsNotificationSent(true);

        return toResponse(focusSessionRepository.save(session));
    }

    @Transactional
    @Scheduled(fixedDelayString = "${focus-session.expire-check-delay-ms:5000}")
    // Job nền: quét phiên quá hạn và tự động complete, đồng thời cộng thời gian vào task.
    public void completeExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        // Tự động đóng các phiên đã hết giờ theo cấu hình scheduler.
        List<FocusSession> expiredSessions = focusSessionRepository.findByIsCompletedFalseAndEndTimeLessThanEqual(now);

        for (FocusSession session : expiredSessions) {
            session.setIsCompleted(true);

            Task task = session.getTask();
            Integer totalFocusTime = task.getTotalFocusTime();
            int currentTotalFocusTime = totalFocusTime == null ? 0 : totalFocusTime;
            Integer duration = session.getActualDuration();
            int safeDuration = duration == null ? 0 : duration;
            task.setTotalFocusTime(currentTotalFocusTime + safeDuration);
        }

        if (!expiredSessions.isEmpty()) {
            focusSessionRepository.saveAll(expiredSessions);
        }
    }

    @Transactional
    // Trả về các thông báo "hết giờ" chưa gửi của user và đánh dấu đã gửi.
    public List<FocusSessionTimeoutNotificationDto> getTimeoutNotifications(UUID userId) {
        List<FocusSession> sessions = focusSessionRepository
            .findByUser_IdAndIsCompletedTrueAndIsNotificationSentFalseOrderByEndTimeAsc(userId);

        // Trả danh sách thông báo và đánh dấu đã gửi để tránh lặp lại.
        List<FocusSessionTimeoutNotificationDto> notifications = sessions.stream()
            .map(session -> new FocusSessionTimeoutNotificationDto(
                session.getId(),
                session.getTask().getId(),
                session.getTask().getTitle(),
                session.getEndTime(),
                "Phiên làm việc cho task '" + session.getTask().getTitle() + "' đã hết giờ"
            ))
            .toList();

        sessions.forEach(session -> session.setIsNotificationSent(true));
        if (!sessions.isEmpty()) {
            focusSessionRepository.saveAll(sessions);
        }

        return notifications;
    }

    // Lấy session theo id, ném lỗi 404 nếu không tồn tại.
    private FocusSession requireFocusSession(UUID id) {
        return focusSessionRepository.findById(id)
            .orElseThrow(() -> new com.workfarm.timemanagement.exception.ResourceNotFoundException("Focus session not found: " + id));
    }

    // Đảm bảo chỉ thao tác pause/cancel trên phiên đang chạy.
    private void ensureSessionIsRunning(FocusSession session) {
        if (Boolean.TRUE.equals(session.getIsCompleted())) {
            throw new IllegalStateException("Focus session is already finished");
        }
    }

    // Convert entity sang DTO để trả về API response.
    private FocusSessionResponseDto toResponse(FocusSession focusSession) {
        return new FocusSessionResponseDto(
            focusSession.getId(),
            focusSession.getUser().getId(),
            focusSession.getTask().getId(),
            focusSession.getStartTime(),
            focusSession.getEndTime(),
            focusSession.getActualDuration(),
            focusSession.getSessionType(),
            focusSession.getIsCompleted()
        );
    }
}
