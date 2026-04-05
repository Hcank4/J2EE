package com.example.demo.controllers;

import com.example.demo.entities.UserRole;
import com.example.demo.services.LookupService;
import com.example.demo.utils.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController extends BaseController {
    public DashboardController(LookupService lookupService) {
        super(lookupService);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (SessionUtil.currentUser(session) == null) return "redirect:/login";
        if (SessionUtil.hasRole(session, UserRole.admin)) return "redirect:/admin";
        if (SessionUtil.hasRole(session, UserRole.employer)) return "redirect:/employer";
        return "redirect:/user";
    }
}
