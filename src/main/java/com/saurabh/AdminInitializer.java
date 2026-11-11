package com.saurabh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.saurabh.ENUMS.Role;
import com.saurabh.Entity.User;
import com.saurabh.repository.UserRepository;

@Component
@Order(1)
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@gmail.com}")
    private String adminEmail;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Value("${app.admin.phone:8595886658}")
    private String adminPhone;

    @Value("${app.admin.fullName:SAURABH}")
    private String adminFullName;

    @Override
    public void run(String... args) {
        String normalizedEmail = adminEmail != null ? adminEmail.trim().toLowerCase() : "admin@gmail.com";
        if (userRepository.findByEmail(normalizedEmail).isEmpty()) {
            User admin = new User();
            admin.setEmail(normalizedEmail);
            admin.setPasswordHash(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setPhone(adminPhone);
            admin.setFullName(adminFullName);
            admin.setIsEmailVerified(true);

            userRepository.save(admin);
            logger.info("Admin user initialized successfully with email: {}", normalizedEmail);
        }
    }
}
