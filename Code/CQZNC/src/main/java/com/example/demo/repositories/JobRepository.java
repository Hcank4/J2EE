package com.example.demo.repositories;

import com.example.demo.entities.Job;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("""
        SELECT j FROM Job j
        WHERE j.status = 1
          AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:careerId IS NULL OR j.career.id = :careerId)
          AND (:jobTypeId IS NULL OR j.jobType.id = :jobTypeId)
          AND (:province IS NULL OR LOWER(j.province) LIKE LOWER(CONCAT('%', :province, '%')))
        ORDER BY COALESCE(j.featured, 0) DESC, j.id DESC
    """)
    List<Job> search(String keyword, Long careerId, Long jobTypeId, String province);

    @Query("""
        SELECT j FROM Job j
        LEFT JOIN FETCH j.career
        LEFT JOIN FETCH j.jobType
        LEFT JOIN FETCH j.employer
        WHERE j.id = :id
    """)
    Optional<Job> findDetailById(Long id);

    List<Job> findTop10ByStatusOrderByFeaturedDescIdDesc(Integer status);

    List<Job> findByEmployerOrderByIdDesc(User employer);
}
