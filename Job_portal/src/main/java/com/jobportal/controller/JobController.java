package com.jobportal.controller;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class JobController {

    private final JobService jobService;
    private final UserService userService;

    public JobController(JobService jobService, UserService userService) {
        this.jobService = jobService;
        this.userService = userService;
    }

    @GetMapping("/jobs")
    public String listJobs(@RequestParam(required = false) String keyword, 
                           @RequestParam(required = false) String location, 
                           Model model) {
        if ((keyword != null && !keyword.isEmpty()) || (location != null && !location.isEmpty())) {
            model.addAttribute("jobs", jobService.searchJobs(keyword, location));
        } else {
            model.addAttribute("jobs", jobService.getAllJobs());
        }
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        return "job-list";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetails(@PathVariable Long id, Model model) {
        Job job = jobService.getJobById(id).orElseThrow();
        model.addAttribute("job", job);
        return "job-details";
    }

    @GetMapping("/employer/post-job")
    public String showPostJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "post-job";
    }

    @PostMapping("/employer/post-job")
    public String postJob(@Valid @ModelAttribute("job") Job job, BindingResult result, Authentication authentication) {
        if (result.hasErrors()) {
            return "post-job";
        }
        User employer = userService.findByUsername(authentication.getName()).orElseThrow();
        job.setEmployer(employer);
        jobService.saveJob(job);
        return "redirect:/dashboard?jobPosted";
    }

    @PostMapping("/employer/delete-job/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return "redirect:/dashboard?jobDeleted";
    }
}
