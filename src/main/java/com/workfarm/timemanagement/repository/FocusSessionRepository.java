package com.workfarm.timemanagement.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workfarm.timemanagement.entity.FocusSession;

public interface FocusSessionRepository extends JpaRepository<FocusSession, UUID> {
	boolean existsByUser_IdAndIsCompletedFalseAndEndTimeAfter(UUID userId, LocalDateTime now);

	List<FocusSession> findByIsCompletedFalseAndEndTimeLessThanEqual(LocalDateTime now);

	List<FocusSession> findByUser_IdAndIsCompletedTrueAndIsNotificationSentFalseOrderByEndTimeAsc(UUID userId);

	List<FocusSession> findByUser_IdOrderByStartTimeDesc(UUID userId);
}
