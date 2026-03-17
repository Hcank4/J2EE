package com.HongCan.course_registration.repository;

import com.HongCan.course_registration.entity.Course;
import com.HongCan.course_registration.entity.Enrollment;
import com.HongCan.course_registration.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);
    List<Enrollment> findByStudent(Student student);
}