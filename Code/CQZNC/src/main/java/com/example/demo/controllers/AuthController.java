package com.example.demo.controllers;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.services.AuthService;
import com.example.demo.services.LookupService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController extends BaseController {

    private final AuthService authService;

    public AuthController(LookupService lookupService, AuthService authService) {
        super(lookupService);
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String doLogin(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                          BindingResult result,
                          HttpSession session,
                          Model model,
                          RedirectAttributes ra) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Vui lòng nhập đúng email và mật khẩu");
            return "auth/login";
        }

        try {
            User user = authService.login(loginRequest);
            authService.saveSession(session, user);
            ra.addFlashAttribute("success", "Đăng nhập thành công");

            if (user.getRole() == UserRole.admin) {
                return "redirect:/admin";
            }
            if (user.getRole() == UserRole.employer) {
                return "redirect:/employer";
            }
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String register(HttpSession session, Model model) {
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                             BindingResult result,
                             Model model,
                             RedirectAttributes ra) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(registerRequest);
            ra.addFlashAttribute("success", "Đăng ký thành công, vui lòng đăng nhập");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("success", "Đăng xuất thành công");
        return "redirect:/";
    }
}
