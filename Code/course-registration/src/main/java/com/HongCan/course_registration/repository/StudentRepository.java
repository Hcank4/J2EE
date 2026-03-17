package com.HongCan.course_registration.repository;

import com.HongCan.course_registration.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    Optional<Student> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}