package com.HongCan.course_registration.service;

import com.HongCan.course_registration.entity.Course;
import com.HongCan.course_registration.entity.Enrollment;
import com.HongCan.course_registration.entity.Student;
import com.HongCan.course_registration.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public void enroll(Student student, Course course) {
        boolean existed = enrollmentRepository.findByStudentAndCourse(student, course).isPresent();
        if (existed) {
            throw new RuntimeException("Bạn đã đăng ký học phần này rồi");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollDate(LocalDateTime.now());

        enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getMyEnrollments(Student student) {
        return enrollmentRepository.findByStudent(student);
    }
}