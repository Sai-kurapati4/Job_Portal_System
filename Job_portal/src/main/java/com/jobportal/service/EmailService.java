package com.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("noreply@jobportal.com");
                message.setTo(toEmail);
                message.setSubject("Your OTP Code for Job Portal");
                message.setText("Welcome to Job Portal!\n\nYour OTP code is: " + otp + "\n\nThis code will expire in 10 minutes.");
                
                mailSender.send(message);
                System.out.println("OTP Email sent to: " + toEmail);
            } else {
                // Fallback for local testing without mail credentials
                System.out.println("\n========== OTP MOCK EMAIL ==========");
                System.out.println("To: " + toEmail);
                System.out.println("Subject: Your OTP Code for Job Portal");
                System.out.println("Message: Welcome to Job Portal! Your OTP code is: " + otp);
                System.out.println("====================================\n");
            }
        } catch (Exception e) {
            System.err.println("Failed to send email. Falling back to console logging.");
            System.out.println("\n========== OTP MOCK EMAIL ==========");
            System.out.println("To: " + toEmail);
            System.out.println("Subject: Your OTP Code for Job Portal");
            System.out.println("Message: Welcome to Job Portal! Your OTP code is: " + otp);
            System.out.println("====================================\n");
        }
    }
}
