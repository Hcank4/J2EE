package com.HongCan.course_registration.controller;

import com.HongCan.course_registration.dto.RegisterRequest;
import com.HongCan.course_registration.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final StudentService studentService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            studentService.registerStudent(request);
            model.addAttribute("success", "Đăng ký thành công, vui lòng đăng nhập");
            model.addAttribute("registerRequest", new RegisterRequest());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "register";
    }
}