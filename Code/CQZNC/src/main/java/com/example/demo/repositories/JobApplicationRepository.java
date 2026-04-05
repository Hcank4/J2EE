package com.example.demo.repositories;

import com.example.demo.entities.Job;
import com.example.demo.entities.JobApplication;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserOrderByIdDesc(User user);
    List<JobApplication> findByEmployerOrderByIdDesc(User employer);
    List<JobApplication> findByJobOrderByIdDesc(Job job);
    Optional<JobApplication> findByJobAndUser(Job job, User user);
    boolean existsByUserAndCvPath(User user, String cvPath);
    long countByUser(User user);
    long countByEmployer(User employer);
}
