package com.HongCan.course_registration.repository;

import com.HongCan.course_registration.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}