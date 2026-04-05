package com.example.demo.services;

import com.example.demo.dto.request.JobForm;
import com.example.demo.entities.*;
import com.example.demo.repositories.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;
    private final JobTypeRepository jobTypeRepository;
    private final SavedJobRepository savedJobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public JobService(JobRepository jobRepository,
                      UserRepository userRepository,
                      CareerRepository careerRepository,
                      JobTypeRepository jobTypeRepository,
                      SavedJobRepository savedJobRepository,
                      JobApplicationRepository jobApplicationRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.careerRepository = careerRepository;
        this.jobTypeRepository = jobTypeRepository;
        this.savedJobRepository = savedJobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public List<Job> search(String keyword, Long careerId, Long jobTypeId, String province) {
        return jobRepository.search(blank(keyword), careerId, jobTypeId, blank(province));
    }

    public Job getDetail(Long id) {
        return jobRepository.findDetailById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy công việc"));
    }

    public List<Job> featuredJobs() {
        return jobRepository.findTop10ByStatusOrderByFeaturedDescIdDesc(1);
    }

    public Job create(JobForm form, User employer) {
        Job job = new Job();
        map(job, form, employer);
        job.setStatus(0);
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());
        return jobRepository.save(job);
    }

    public Job update(Long id, JobForm form, User employer) {
        Job job = getDetail(id);
        if (!job.getEmployer().getId().equals(employer.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền sửa tin này");
        }
        map(job, form, employer);
        job.setUpdatedAt(LocalDateTime.now());
        return jobRepository.save(job);
    }

    public void map(Job job, JobForm form, User employer) {
        job.setTitle(form.getTitle());
        job.setCareer(careerRepository.findById(form.getCareerId()).orElseThrow());
        job.setJobType(jobTypeRepository.findById(form.getJobTypeId()).orElseThrow());
        job.setEmployer(employer);
        job.setVacancy(form.getVacancy());
        job.setSalary(form.getSalary());
        job.setJobLevel(form.getJobLevel());
        job.setDescription(form.getDescription());
        job.setBenefits(form.getBenefits());
        job.setResponsibility(form.getResponsibility());
        job.setQualifications(form.getQualifications());
        job.setKeywords(form.getKeywords());
        job.setExperience(form.getExperience());
        job.setCompanyName(form.getCompanyName() == null || form.getCompanyName().isBlank()
                ? employer.getCompanyName()
                : form.getCompanyName());
        job.setProvince(form.getProvince());
        job.setDistrict(form.getDistrict());
        job.setWards(form.getWards());
        job.setLocationDetail(form.getLocationDetail());
        job.setCompanyWebsite(form.getCompanyWebsite() == null || form.getCompanyWebsite().isBlank()
                ? employer.getCompanyWebsite()
                : form.getCompanyWebsite());
        job.setExpirationDate(form.getExpirationDate());
        job.setFeatured(form.getIsFeatured() == null ? 0 : form.getIsFeatured());
    }

    public void toggleSave(User user, Job job) {
        if (user == null || user.getId() == null || job == null || job.getId() == null) {
            throw new IllegalArgumentException("Dữ liệu không hợp lệ để lưu việc làm");
        }
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản ứng viên"));
        Job managedJob = jobRepository.findById(job.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy công việc"));

        savedJobRepository.findByUserIdAndJobId(managedUser.getId(), managedJob.getId()).ifPresentOrElse(savedJobRepository::delete, () -> {
            SavedJob saved = new SavedJob();
            Long nextId = savedJobRepository.findTopByOrderByIdDesc()
                    .map(SavedJob::getId)
                    .map(id -> id + 1)
                    .orElse(1L);
            saved.setId(nextId);
            saved.setUser(managedUser);
            saved.setJob(managedJob);
            saved.setCreatedAt(LocalDateTime.now());
            saved.setUpdatedAt(LocalDateTime.now());
            savedJobRepository.save(saved);
        });
    }

    public boolean isSaved(User user, Job job) {
        return savedJobRepository.findByUserAndJob(user, job).isPresent();
    }

    public List<SavedJob> savedJobs(User user) {
        return savedJobRepository.findByUserOrderByIdDesc(user);
    }

    public List<Job> jobsByEmployer(User employer) {
        return jobRepository.findByEmployerOrderByIdDesc(employer);
    }

    public void approve(Long jobId) {
        Job job = getDetail(jobId);
        job.setStatus(1);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    public void reject(Long jobId) {
        Job job = getDetail(jobId);
        job.setStatus(0);
        job.setUpdatedAt(LocalDateTime.now());
        jobRepository.save(job);
    }

    private String blank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
