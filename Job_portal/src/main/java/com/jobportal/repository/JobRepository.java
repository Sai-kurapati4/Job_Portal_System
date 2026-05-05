package com.jobportal.repository;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByEmployerOrderByPostedAtDesc(User employer);
    
    @Query("SELECT j FROM Job j WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.skillsRequired) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.employer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Job> searchJobs(@Param("keyword") String keyword);
    
    @Query("SELECT j FROM Job j WHERE (LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.skillsRequired) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(j.employer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Job> searchJobsWithLocation(@Param("keyword") String keyword, @Param("location") String location);
    
    List<Job> findAllByOrderByPostedAtDesc();
}
