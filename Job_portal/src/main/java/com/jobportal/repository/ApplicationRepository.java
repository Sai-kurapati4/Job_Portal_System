package com.jobportal.repository;

import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserOrderByAppliedAtDesc(User user);
    List<Application> findByJobOrderByAppliedAtDesc(Job job);
    boolean existsByUserAndJob(User user, Job job);
}
