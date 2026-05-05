package com.jobportal.config;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedDatabase(UserRepository userRepository, JobRepository jobRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("techcorp").isEmpty()) {
                // Create Employer
                User employer = new User();
                employer.setUsername("techcorp");
                employer.setPassword(passwordEncoder.encode("password"));
                employer.setEmail("hr@techcorp.com");
                employer.setFullName("TechCorp Solutions");
                employer.setRole("EMPLOYER");
                employer.setEnabled(true);
                userRepository.save(employer);

                // Create Student
                User student = new User();
                student.setUsername("student1");
                student.setPassword(passwordEncoder.encode("password"));
                student.setEmail("student@example.com");
                student.setFullName("John Doe");
                student.setRole("STUDENT");
                student.setEnabled(true);
                userRepository.save(student);

                // Create Jobs
                Job job1 = new Job();
                job1.setTitle("Senior Full Stack Java Developer");
                job1.setDescription("We are looking for an experienced Full Stack Java Developer with strong Spring Boot and React/Thymeleaf skills. You will be responsible for leading our core platform development.");
                job1.setCategory("IT & Software");
                job1.setLocation("San Francisco, CA");
                job1.setSalary("$130,000 - $160,000");
                job1.setSkillsRequired("Java, Spring Boot, React, MySQL");
                job1.setApplicationDeadline(LocalDate.now().plusDays(30));
                job1.setEmployer(employer);
                jobRepository.save(job1);

                Job job2 = new Job();
                job2.setTitle("Data Scientist / AI Engineer");
                job2.setDescription("Join our AI research team to build cutting-edge predictive models. Must have a strong background in Machine Learning, deep learning frameworks, and Python.");
                job2.setCategory("Engineering");
                job2.setLocation("New York, NY");
                job2.setSalary("$140,000 - $175,000");
                job2.setSkillsRequired("Python, TensorFlow, PyTorch, SQL");
                job2.setApplicationDeadline(LocalDate.now().plusDays(15));
                job2.setEmployer(employer);
                jobRepository.save(job2);

                Job job3 = new Job();
                job3.setTitle("Marketing Specialist");
                job3.setDescription("Seeking a creative Marketing Specialist to drive our digital campaigns. Experience with SEO, content creation, and analytics tools is highly desired.");
                job3.setCategory("Marketing");
                job3.setLocation("Austin, TX");
                job3.setSalary("$70,000 - $90,000");
                job3.setSkillsRequired("SEO, Content Marketing, Google Analytics");
                job3.setApplicationDeadline(LocalDate.now().plusDays(10));
                job3.setEmployer(employer);
                jobRepository.save(job3);
                
                System.out.println("========== DATABASE SEEDED SUCCESSFULLY ==========");
                System.out.println("Employer Login: techcorp / password");
                System.out.println("Student Login: student1 / password");
            }
        };
    }
}
