package com.example.demo.repositories;

import com.example.demo.entities.Job;
import com.example.demo.entities.SavedJob;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    Optional<SavedJob> findByUserAndJob(User user, Job job);
    Optional<SavedJob> findByUserIdAndJobId(Long userId, Long jobId);
    Optional<SavedJob> findTopByOrderByIdDesc();
    List<SavedJob> findByUserOrderByIdDesc(User user);
    long countByUser(User user);
}
