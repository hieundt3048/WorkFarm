package com.workfarm.timemanagement.repository;

import com.workfarm.timemanagement.entity.FocusSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FocusSessionRepository extends JpaRepository<FocusSession, UUID> {
}
