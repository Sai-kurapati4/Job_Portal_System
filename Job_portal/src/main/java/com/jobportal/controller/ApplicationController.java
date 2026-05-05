package com.jobportal.controller;

import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JobService jobService;
    private final UserService userService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    public ApplicationController(ApplicationService applicationService, JobService jobService, UserService userService) {
        this.applicationService = applicationService;
        this.jobService = jobService;
        this.userService = userService;
    }

    @PostMapping("/student/apply/{jobId}")
    public String applyForJob(@PathVariable Long jobId,
                             @RequestParam("resume") MultipartFile resume,
                             Authentication authentication) throws IOException {
        User student = userService.findByUsername(authentication.getName()).orElseThrow();
        Job job = jobService.getJobById(jobId).orElseThrow();

        if (applicationService.hasAlreadyApplied(student, job)) {
            return "redirect:/jobs/" + jobId + "?alreadyApplied";
        }

        String filename = UUID.randomUUID() + "_" + resume.getOriginalFilename();
        Path path = Paths.get(uploadDir + filename);
        Files.createDirectories(path.getParent());
        Files.write(path, resume.getBytes());

        Application application = new Application();
        application.setJob(job);
        application.setUser(student);
        application.setStatus("APPLIED");
        application.setResumeUrl(filename);

        applicationService.applyForJob(application);

        return "redirect:/dashboard?applied";
    }

    @GetMapping("/employer/applicants/{jobId}")
    public String viewApplicants(@PathVariable Long jobId, Model model) {
        Job job = jobService.getJobById(jobId).orElseThrow();
        model.addAttribute("job", job);
        model.addAttribute("applicants", applicationService.getApplicationsByJob(job));
        return "applicants";
    }

    @PostMapping("/employer/application/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status) {
        Application app = applicationService.getApplicationById(id).orElseThrow();
        applicationService.updateStatus(id, status);
        return "redirect:/employer/applicants/" + app.getJob().getId();
    }
}
