package com.saurabh;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.saurabh.ENUMS.*;
import com.saurabh.Entity.*;
import com.saurabh.repository.*;

@Component
@Order(2) // runs after AdminInitializer
public class DataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private JobseekerRepository jobseekerRepository;
    @Autowired private EmployerRepository employerRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedJobSeekers();
        seedEmployers();
    }

    @SuppressWarnings("null")
	private void seedJobSeekers() {
        for (int i = 1; i <= 50; i++) {
            String email = "jobseeker" + i + "@gmail.com";
            if (userRepository.findByEmail(email).isPresent()) continue;

            User user = User.builder()
                    .fullName("JobSeeker " + i)
                    .email(email)
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role(Role.JOBSEEKER)
                    .phone("90000000" + String.format("%02d", i))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            userRepository.save(user);

            JobSeeker jobSeeker = JobSeeker.builder()
                    .user(user)
                    .headline("Developer " + i)
                    .summary("Experienced developer " + i)
                    .location("Delhi")
                    .yearsOfExperience(i % 10)
                    .expectedSalary(BigDecimal.valueOf(50000 + (i * 1000)))
                    .availability(Availability.OPEN_TO_WORK)
                    .build();
            jobseekerRepository.save(jobSeeker);
        }
        System.out.println("✅ 50 JobSeekers seeded");
    }

    @SuppressWarnings("null")
	private void seedEmployers() {
        for (int i = 1; i <= 50; i++) {
            String email = "employer" + i + "@gmail.com";
            if (userRepository.findByEmail(email).isPresent()) continue;

            User user = User.builder()
                    .fullName("Employer " + i)
                    .email(email)
                    .passwordHash(passwordEncoder.encode("password123"))
                    .role(Role.EMPLOYER)
                    .phone("80000000" + String.format("%02d", i))
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            userRepository.save(user);

            Employer employer = Employer.builder()
                    .user(user)
                    .companyName("Company " + i)
                    .description("Description for company " + i)
                    .companySize(CompanySize.values()[i % CompanySize.values().length])
                    .isVerified(i % 2 == 0) // every alternate employer is verified
                    .build();
            employerRepository.save(employer);
        }
        System.out.println("✅ 50 Employers seeded");
    }
}