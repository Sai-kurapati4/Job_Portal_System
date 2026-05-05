package com.jobportal.service;

import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public Application applyForJob(Application application) {
        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsByUser(User user) {
        return applicationRepository.findByUserOrderByAppliedAtDesc(user);
    }

    public List<Application> getApplicationsByJob(Job job) {
        return applicationRepository.findByJobOrderByAppliedAtDesc(job);
    }

    public boolean hasAlreadyApplied(User user, Job job) {
        return applicationRepository.existsByUserAndJob(user, job);
    }

    public Optional<Application> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public void updateStatus(Long id, String status) {
        Application app = applicationRepository.findById(id).orElseThrow();
        app.setStatus(status);
        applicationRepository.save(app);
    }
}
