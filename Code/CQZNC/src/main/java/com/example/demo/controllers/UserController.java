package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.repositories.UserRepository;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final UserRepository userRepository;

    public UserController(LookupService lookupService,
                          JobService jobService,
                          ApplicationService applicationService,
                          UserRepository userRepository) {
        super(lookupService);
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.userRepository = userRepository;
    }

    private User require(HttpSession session) {
        User user = SessionUtil.currentUser(session);
        if (user == null || user.getRole() != UserRole.user) {
            return null;
        }
        return user;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model, RedirectAttributes ra) {
        User user = require(session);
        if (user == null) {
            ra.addFlashAttribute("error", "Vui long dang nhap tai khoan ung vien");
            return "redirect:/login";
        }
        model.addAttribute("savedJobs", jobService.savedJobs(user));
        model.addAttribute("applications", applicationService.byUser(user));
        return "user/dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model, RedirectAttributes ra) {
        User user = require(session);
        if (user == null) {
            ra.addFlashAttribute("error", "Vui long dang nhap tai khoan ung vien");
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        model.addAttribute("cvItems", applicationService.listUserCvItems(user));
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(HttpSession session,
                                @RequestParam String fullname,
                                @RequestParam String mobile,
                                @RequestParam(required = false) String province,
                                @RequestParam(required = false) String district,
                                @RequestParam(required = false) String wards,
                                @RequestParam(required = false) String locationDetail,
                                @RequestParam(required = false) MultipartFile avatarFile,
                                RedirectAttributes ra) {
        User user = require(session);
        if (user == null) {
            ra.addFlashAttribute("error", "Vui long dang nhap tai khoan ung vien");
            return "redirect:/login";
        }
        user.setFullname(fullname);
        user.setMobile(mobile);
        user.setProvince(province);
        user.setDistrict(district);
        user.setWards(wards);
        user.setLocationDetail(locationDetail);

        String avatar = storeAvatar(avatarFile, user.getEmail());
        if (avatar != null) {
            user.setImage(avatar);
        }

        userRepository.save(user);
        session.setAttribute("currentUser", user);
        ra.addFlashAttribute("success", "Cap nhat ho so ung vien thanh cong");
        return "redirect:/user/profile";
    }

    @PostMapping("/profile/cv")
    public String uploadCv(HttpSession session,
                           @RequestParam("cvFile") MultipartFile cvFile,
                           RedirectAttributes ra) {
        User user = require(session);
        if (user == null) {
            ra.addFlashAttribute("error", "Vui long dang nhap tai khoan ung vien");
            return "redirect:/login";
        }
        try {
            applicationService.uploadUserCv(user, cvFile);
            ra.addFlashAttribute("success", "Tai CV thanh cong");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/profile/cv/delete")
    public String deleteCv(HttpSession session,
                           @RequestParam("cvName") String cvName,
                           RedirectAttributes ra) {
        User user = require(session);
        if (user == null) {
            ra.addFlashAttribute("error", "Vui long dang nhap tai khoan ung vien");
            return "redirect:/login";
        }
        try {
            applicationService.deleteUserCv(user, cvName);
            ra.addFlashAttribute("success", "Xoa CV thanh cong");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/applications/{id}/cv")
    public String openCv(@PathVariable Long id,
                         @RequestHeader(value = "Referer", required = false) String referer,
                         HttpSession session,
                         RedirectAttributes ra) {
        User user = require(session);
        if (user == null) {
            ra.addFlashAttribute("error", "Vui long dang nhap tai khoan ung vien");
            return "redirect:/login";
        }
        try {
            String cvUrl = applicationService.resolveCvForUser(id, user);
            return "redirect:" + cvUrl;
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            if (referer != null && !referer.isBlank()) {
                return "redirect:" + referer;
            }
            return "redirect:/user";
        }
    }
}
