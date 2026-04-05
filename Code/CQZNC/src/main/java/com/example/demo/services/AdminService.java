package com.example.demo.services;

import com.example.demo.entities.Job;
import com.example.demo.entities.User;
import com.example.demo.entities.UserRole;
import com.example.demo.repositories.JobApplicationRepository;
import com.example.demo.repositories.JobRepository;
import com.example.demo.repositories.SavedJobRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final SavedJobRepository savedJobRepository;

    public AdminService(UserRepository userRepository, JobRepository jobRepository, JobApplicationRepository jobApplicationRepository, SavedJobRepository savedJobRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.savedJobRepository = savedJobRepository;
    }

    public Map<String, Long> dashboard() {
        Map<String, Long> m = new HashMap<>();
        m.put("users", (long) userRepository.findByRoleOrderByIdDesc(UserRole.user).size());
        m.put("employers", (long) userRepository.findByRoleOrderByIdDesc(UserRole.employer).size());
        m.put("jobs", (long) jobRepository.findAll().size());
        m.put("applications", (long) jobApplicationRepository.findAll().size());
        m.put("savedJobs", (long) savedJobRepository.findAll().size());
        return m;
    }

    public List<User> usersByRole(UserRole role) {
        return userRepository.findByRoleOrderByIdDesc(role);
    }

    public List<Job> allJobs() {
        return jobRepository.findAll();
    }

    public void toggleStatus(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setStatus(user.getStatus() != null && user.getStatus() == 1 ? 0 : 1);
        userRepository.save(user);
    }
}
