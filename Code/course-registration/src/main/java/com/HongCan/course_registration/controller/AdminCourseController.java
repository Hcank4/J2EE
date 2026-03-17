package com.HongCan.course_registration.controller;

import com.HongCan.course_registration.entity.Category;
import com.HongCan.course_registration.entity.Course;
import com.HongCan.course_registration.repository.CategoryRepository;
import com.HongCan.course_registration.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/courses")
@RequiredArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses("", 0, 100).getContent());
        return "admin-course-list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin-course-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Course course, @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            course.setCategory(category);
        } else {
            course.setCategory(null);
        }

        courseService.save(course);
        return "redirect:/admin/courses";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.findById(id));
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin-course-form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        courseService.deleteById(id);
        return "redirect:/admin/courses";
    }
}