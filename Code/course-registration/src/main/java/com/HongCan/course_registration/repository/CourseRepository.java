package com.HongCan.course_registration.repository;

import com.HongCan.course_registration.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}