package com.example.demo.controllers;

import com.example.demo.services.JobService;
import com.example.demo.services.LookupService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController extends BaseController {

    private final JobService jobService;

    public HomeController(LookupService lookupService, JobService jobService) {
        super(lookupService);
        this.jobService = jobService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("featuredJobs", jobService.featuredJobs());
        return "index";
    }
}