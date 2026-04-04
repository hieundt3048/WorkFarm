package com.workfarm.timemanagement.repository;

import com.workfarm.timemanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
