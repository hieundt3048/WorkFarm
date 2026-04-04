package com.workfarm.timemanagement.repository;

import com.workfarm.timemanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
}
