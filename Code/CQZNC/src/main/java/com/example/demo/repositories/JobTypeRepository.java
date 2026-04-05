package com.example.demo.repositories;

import com.example.demo.entities.JobType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobTypeRepository extends JpaRepository<JobType, Long> {
    List<JobType> findByStatusOrderByNameAsc(Integer status);
}
