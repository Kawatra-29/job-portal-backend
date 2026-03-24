package com.saurabh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.saurabh.ENUMS.Role;
import com.saurabh.Entity.User;
import com.saurabh.repository.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

	@Override
    public void run(String... args) {

        if(userRepository.findByEmail("admin@gmail.com").isEmpty()) {

            User admin = new User();
            admin.setEmail("admin@gmail.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setPhone("8595886658");
            admin.setFullName("SAURABH");

            userRepository.save(admin);
        }
       
    }
}