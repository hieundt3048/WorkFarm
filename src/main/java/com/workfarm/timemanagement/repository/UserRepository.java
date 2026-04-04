package com.workfarm.timemanagement.repository;

import com.workfarm.timemanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
