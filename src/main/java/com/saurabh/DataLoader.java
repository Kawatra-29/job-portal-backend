package com.saurabh;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.saurabh.Entity.Skill;
import com.saurabh.repository.SkillRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadSkills(SkillRepository skillRepository) {
        return args -> {

            List<String> skills = List.of(
                    "Java",
                    "Spring Boot",
                    "Hibernate",
                    "SQL",
                    "REST API",
                    "Docker",
                    "Git",
                    "Microservices"
            );

            for (String skillName : skills) {

                if (skillRepository.findByName(skillName).isEmpty()) {
                    Skill skill = new Skill();
                    skill.setName(skillName);
                    skillRepository.save(skill);
                }

            }

        };
    }

}