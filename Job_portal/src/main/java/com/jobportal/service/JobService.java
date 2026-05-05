package com.jobportal.service;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAllByOrderByPostedAtDesc();
    }

    public List<Job> getJobsByEmployer(User employer) {
        return jobRepository.findByEmployerOrderByPostedAtDesc(employer);
    }

    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    public List<Job> searchJobs(String keyword, String location) {
        if (location != null && !location.isEmpty()) {
            return jobRepository.searchJobsWithLocation(keyword, location);
        }
        return jobRepository.searchJobs(keyword);
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}
