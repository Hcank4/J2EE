package com.HongCan.course_registration.service;

import com.HongCan.course_registration.entity.Course;
import com.HongCan.course_registration.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public Page<Course> getAllCourses(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if (keyword != null && !keyword.trim().isEmpty()) {
            return courseRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
        }
        return courseRepository.findAll(pageable);
    }

    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy course"));
    }

    public Course save(Course course) {
        return courseRepository.save(course);
    }

    public void deleteById(Long id) {
        courseRepository.deleteById(id);
    }
}