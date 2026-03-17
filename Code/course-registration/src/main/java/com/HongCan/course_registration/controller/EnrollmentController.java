package com.HongCan.course_registration.controller;

import com.HongCan.course_registration.entity.Course;
import com.HongCan.course_registration.entity.Enrollment;
import com.HongCan.course_registration.entity.Student;
import com.HongCan.course_registration.service.CourseService;
import com.HongCan.course_registration.service.EnrollmentService;
import com.HongCan.course_registration.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CourseService courseService;
    private final StudentService studentService;

    @PostMapping("/enroll/{courseId}")
    public String enroll(@PathVariable Long courseId, Authentication authentication) {
        String username = authentication.getName();
        Student student = studentService.findByUsername(username);
        Course course = courseService.findById(courseId);

        try {
            enrollmentService.enroll(student, course);
        } catch (Exception e) {
            return "redirect:/home?error=" + e.getMessage();
        }

        return "redirect:/home?success=Đăng ký học phần thành công";
    }

    @GetMapping("/my-courses")
    public String myCourses(Authentication authentication, Model model) {
        String username = authentication.getName();
        Student student = studentService.findByUsername(username);
        List<Enrollment> enrollments = enrollmentService.getMyEnrollments(student);

        model.addAttribute("enrollments", enrollments);
        return "my-courses";
    }
}