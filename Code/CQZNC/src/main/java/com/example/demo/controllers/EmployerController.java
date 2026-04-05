package com.example.demo.controllers;

import com.example.demo.dto.request.JobForm;
import com.example.demo.entities.Job;
import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.ApplicationService;
import com.example.demo.services.JobService;
import com.example.demo.services.LookupService;
import com.example.demo.utils.SessionUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employer")
public class EmployerController extends BaseController {
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final UserRepository userRepository;

    public EmployerController(LookupService lookupService,
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
        if (user == null || user.getRole() != UserRole.employer) {
            return null;
        }
        return user;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model, RedirectAttributes ra) {
        User employer = require(session);
        if (employer == null) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập tài khoản nhà tuyển dụng");
            return "redirect:/login";
        }
        model.addAttribute("jobs", jobService.jobsByEmployer(employer));
        model.addAttribute("applications", applicationService.byEmployer(employer));
        return "employer/dashboard";
    }

    @GetMapping("/jobs/new")
    public String newJob(Model model, HttpSession session, RedirectAttributes ra) {
        User employer = require(session);
        if (employer == null) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập tài khoản nhà tuyển dụng");
            return "redirect:/login";
        }
        JobForm form = new JobForm();
        form.setCompanyName(employer.getCompanyName());
        model.addAttribute("jobForm", form);
        return "employer/job-form";
    }

    @PostMapping("/jobs/new")
    public String createJob(@Valid @ModelAttribute JobForm jobForm,
                            BindingResult result,
                            HttpSession session,
                            RedirectAttributes ra) {
        User employer = require(session);
        if (employer == null) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập tài khoản nhà tuyển dụng");
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            return "employer/job-form";
        }
        jobService.create(jobForm, employer);
        ra.addFlashAttribute("success", "Đăng tin tuyển dụng thành công, vui lòng chờ duyệt");
        return "redirect:/employer";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJob(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes ra) {
        User employer = require(session);
        if (employer == null) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập tài khoản nhà tuyển dụng");
            return "redirect:/login";
        }
        Job job = jobService.getDetail(id);

        if (!job.getEmployer().getId().equals(employer.getId())) {
            ra.addFlashAttribute("error", "Bạn không có quyền sửa tin này");
            return "redirect:/employer";
        }

        JobForm form = new JobForm();
        form.setTitle(job.getTitle());
        form.setCareerId(job.getCareer().getId());
        form.setJobTypeId(job.getJobType().getId());
        form.setVacancy(job.getVacancy());
        form.setSalary(job.getSalary());
        form.setJobLevel(job.getJobLevel());
        form.setDescription(job.getDescription());
        form.setBenefits(job.getBenefits());
        form.setResponsibility(job.getResponsibility());
        form.setQualifications(job.getQualifications());
        form.setKeywords(job.getKeywords());
        form.setExperience(job.getExperience());
        form.setCompanyName(job.getCompanyName());
        form.setProvince(job.getProvince());
        form.setDistrict(job.getDistrict());
        form.setWards(job.getWards());
        form.setLocationDetail(job.getLocationDetail());
        form.setCompanyWebsite(job.getCompanyWebsite());
        form.setExpirationDate(job.getExpirationDate());
        form.setIsFeatured(job.getFeatured());

        model.addAttribute("jobForm", form);
        model.addAttribute("jobId", id);
        return "employer/job-form";
    }

    @PostMapping("/jobs/{id}/edit")
    public String updateJob(@PathVariable Long id,
                            @Valid @ModelAttribute JobForm jobForm,
                            BindingResult result,
                            HttpSession session,
                            RedirectAttributes ra) {
        User employer = require(session);
        if (employer == null) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập tài khoản nhà tuyển dụng");
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            return "employer/job-form";
        }
        jobService.update(id, jobForm, employer);
        ra.addFlashAttribute("success", "Cập nhật tin tuyển dụng thành công");
        return "redirect:/employer";
    }

    @PostMapping("/applications/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam Integer status,
                               HttpSession session,
                               RedirectAttributes ra) {
        User employer = require(session);
        if (employer == null) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập tài khoản nhà tuyển dụng");
            return "redirect:/login";
        }
        try {
            applicationService.updateStatus(id, status, employer);
            ra.addFlashAttribute("success", "Đã cập nhật trạng thái hồ sơ");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/employer";
    }

    @GetMapping("/applications/{id}/cv")
    public String openCv(@PathVariable Long id,
                         @RequestHeader(value = "Referer", required = false) String referer,
                         HttpSession session,
                         RedirectAttributes ra) {
        User employer = require(session);
        if (employer == null) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập tài khoản nhà tuyển dụng");
            return "redirect:/login";
        }
        try {
            String cvUrl = applicationService.resolveCvForEmployer(id, employer);
            return "redirect:" + cvUrl;
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            if (referer != null && !referer.isBlank()) {
                return "redirect:" + referer;
            }
            return "redirect:/employer";
        }
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model, RedirectAttributes ra) {
        User employer = require(session);
        if (employer == null) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập tài khoản nhà tuyển dụng");
            return "redirect:/login";
        }
        model.addAttribute("user", employer);
        return "employer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(HttpSession session,
                                @RequestParam String fullname,
                                @RequestParam String companyName,
                                @RequestParam String mobile,
                                @RequestParam(required = false) String companyWebsite,
                                @RequestParam(required = false) String province,
                                @RequestParam(required = false) String district,
                                @RequestParam(required = false) String wards,
                                @RequestParam(required = false) String locationDetail,
                                @RequestParam(required = false) MultipartFile avatarFile,
                                RedirectAttributes ra) {
        User user = require(session);
        if (user == null) {
            ra.addFlashAttribute("error", "Vui lòng đăng nhập tài khoản nhà tuyển dụng");
            return "redirect:/login";
        }
        user.setFullname(fullname);
        user.setCompanyName(companyName);
        user.setMobile(mobile);
        user.setCompanyWebsite(companyWebsite);
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
        ra.addFlashAttribute("success", "Cập nhật hồ sơ doanh nghiệp thành công");
        return "redirect:/employer/profile";
    }
}
