package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.services.AdminService;
import com.example.demo.services.JobService;
import com.example.demo.services.LookupService;
import com.example.demo.utils.SessionUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController extends BaseController {
    private final AdminService adminService;
    private final JobService jobService;

    public AdminController(LookupService lookupService, AdminService adminService, JobService jobService) {
        super(lookupService);
        this.adminService = adminService;
        this.jobService = jobService;
    }

    private User require(HttpSession session) {
        User user = SessionUtil.currentUser(session);
        if (user == null || user.getRole() != UserRole.admin) {
            return null;
        }
        return user;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        if (require(session) == null) {
            return "redirect:/login";
        }
        model.addAttribute("stats", adminService.dashboard());
        model.addAttribute("users", adminService.usersByRole(UserRole.user));
        model.addAttribute("employers", adminService.usersByRole(UserRole.employer));
        model.addAttribute("jobs", adminService.allJobs());
        return "admin/dashboard";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (require(session) == null) {
            return "redirect:/login";
        }
        adminService.toggleStatus(id);
        ra.addFlashAttribute("success", "Đã cập nhật trạng thái tài khoản");
        return "redirect:/admin";
    }

    @PostMapping("/jobs/{id}/approve")
    public String approve(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (require(session) == null) {
            return "redirect:/login";
        }
        jobService.approve(id);
        ra.addFlashAttribute("success", "Đã duyệt tin tuyển dụng");
        return "redirect:/admin";
    }

    @PostMapping("/jobs/{id}/reject")
    public String reject(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        if (require(session) == null) {
            return "redirect:/login";
        }
        jobService.reject(id);
        ra.addFlashAttribute("success", "Đã chuyển tin về trạng thái chờ duyệt");
        return "redirect:/admin";
    }
}
