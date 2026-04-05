package com.example.demo.controllers;

import com.example.demo.entities.Job;
import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.services.ApplicationService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/jobs")
public class JobController extends BaseController {
    private final JobService jobService;
    private final ApplicationService applicationService;

    public JobController(LookupService lookupService, JobService jobService, ApplicationService applicationService) {
        super(lookupService);
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long careerId,
                       @RequestParam(required = false) Long jobTypeId,
                       @RequestParam(required = false) String province,
                       Model model) {
        model.addAttribute("jobs", jobService.search(keyword, careerId, jobTypeId, province));
        model.addAttribute("keyword", keyword);
        model.addAttribute("careerId", careerId);
        model.addAttribute("jobTypeId", jobTypeId);
        model.addAttribute("province", province);
        return "jobs/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        Job job = jobService.getDetail(id);
        model.addAttribute("job", job);

        User currentUser = SessionUtil.currentUser(session);
        model.addAttribute("isSaved",
                currentUser != null && currentUser.getRole() == UserRole.user && jobService.isSaved(currentUser, job));
        model.addAttribute("userCvItems",
                currentUser != null && currentUser.getRole() == UserRole.user
                        ? applicationService.listUserCvItems(currentUser)
                        : List.of());

        return "jobs/detail";
    }

    @PostMapping("/{id}/save")
    public String toggleSave(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User user = SessionUtil.currentUser(session);
        if (user == null || user.getRole() != UserRole.user) {
            ra.addFlashAttribute("error", "Vui long dang nhap tai khoan ung vien de luu viec lam");
            return "redirect:/login";
        }

        try {
            jobService.toggleSave(user, jobService.getDetail(id));
            ra.addFlashAttribute("success", "Da cap nhat danh sach viec lam da luu");
        } catch (Exception e) {
            String msg = (e.getMessage() == null || e.getMessage().isBlank())
                    ? "Khong the luu viec lam. Vui long thu lai."
                    : e.getMessage();
            ra.addFlashAttribute("error", msg);
        }
        return "redirect:/jobs/" + id;
    }

    @PostMapping("/{id}/apply")
    public String applyJob(@PathVariable Long id,
                           @RequestParam("selectedCv") String selectedCv,
                           HttpSession session,
                           RedirectAttributes ra) {
        User currentUser = SessionUtil.currentUser(session);

        if (currentUser == null) {
            ra.addFlashAttribute("error", "Vui long dang nhap de ung tuyen");
            return "redirect:/login";
        }

        if (currentUser.getRole() != UserRole.user) {
            ra.addFlashAttribute("error", "Chi ung vien moi co the ung tuyen");
            return "redirect:/jobs/" + id;
        }

        try {
            applicationService.apply(jobService.getDetail(id), currentUser, selectedCv);
            ra.addFlashAttribute("success", "Ung tuyen thanh cong");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/jobs/" + id;
    }

    @GetMapping("/{id}/apply")
    public String applyJobGet(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        User currentUser = SessionUtil.currentUser(session);

        if (currentUser == null) {
            ra.addFlashAttribute("error", "Vui long dang nhap de ung tuyen");
            return "redirect:/login";
        }

        return "redirect:/jobs/" + id;
    }
}
