package com.jobportal.controller;

import com.jobportal.entity.User;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import com.jobportal.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class AuthController {

    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final EmailService emailService;

    public AuthController(UserService userService, JobService jobService, ApplicationService applicationService, EmailService emailService) {
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/jobs";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    private Map<String, String> otpStorage = new ConcurrentHashMap<>();

    @PostMapping("/api/auth/send-otp")
    @ResponseBody
    public String sendOtp(@RequestParam("email") String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp);
        emailService.sendOtpEmail(email, otp);
        return "OTP sent successfully";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               @RequestParam(value = "otp", required = false) String otp,
                               Model model) {
        if (userService.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists");
        }
        if (userService.existsByEmail(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists");
        }

        // Verify OTP
        if (otp == null || !otp.equals(otpStorage.get(user.getEmail()))) {
            model.addAttribute("otpError", "Invalid or missing OTP. Please verify your email.");
            return "register";
        }

        if (result.hasErrors()) {
            return "register";
        }

        user.setEnabled(true);
        userService.saveUser(user);
        otpStorage.remove(user.getEmail()); // clear OTP after successful registration
        return "redirect:/login?success";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        
        if (user.getRole().equals("EMPLOYER")) {
            model.addAttribute("postedJobs", jobService.getJobsByEmployer(user));
        } else if (user.getRole().equals("STUDENT")) {
            model.addAttribute("applications", applicationService.getApplicationsByUser(user));
        }
        
        return "dashboard";
    }
}
