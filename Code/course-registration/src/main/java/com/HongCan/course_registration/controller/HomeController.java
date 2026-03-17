package com.HongCan.course_registration.controller;

import com.HongCan.course_registration.entity.Course;
import com.HongCan.course_registration.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final CourseService courseService;

    @GetMapping({"/", "/home", "/courses"})
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String keyword,
                       Model model) {

        Page<Course> coursePage = courseService.getAllCourses(keyword, page, 5);

        model.addAttribute("coursePage", coursePage);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);

        return "home";
    }
}