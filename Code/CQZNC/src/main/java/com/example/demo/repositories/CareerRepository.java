package com.example.demo.repositories;

import com.example.demo.entities.Career;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Long> {
    List<Career> findByStatusOrderByNameAsc(Integer status);
}