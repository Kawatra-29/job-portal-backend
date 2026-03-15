package com.saurabh.DTOs;

import java.math.BigDecimal;
import java.util.Set;

public record JobSeekerResponseDTO(

        Long id,
        String headline,
        String summary,
        String location,
        Integer yearsOfExperience,
        BigDecimal expectedSalary,
        String availability,
        Set<SkillResponseDto> skills,
        UserResponseDTO user

) {}