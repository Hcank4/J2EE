package com.example.demo.services;

import com.example.demo.entities.Career;
import com.example.demo.entities.JobType;
import com.example.demo.repositories.CareerRepository;
import com.example.demo.repositories.JobTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LookupService {

    private final CareerRepository careerRepository;
    private final JobTypeRepository jobTypeRepository;

    public LookupService(CareerRepository careerRepository, JobTypeRepository jobTypeRepository) {
        this.careerRepository = careerRepository;
        this.jobTypeRepository = jobTypeRepository;
    }

    public List<Career> careers() {
        return careerRepository.findByStatusOrderByNameAsc(1);
    }

    public List<JobType> jobTypes() {
        return jobTypeRepository.findByStatusOrderByNameAsc(1);
    }
}