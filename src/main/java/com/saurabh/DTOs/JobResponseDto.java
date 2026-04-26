package com.saurabh.DTOs;

import java.math.BigDecimal;

import com.saurabh.ENUMS.*;
import java.time.LocalDate;

public record JobResponseDto(
        Long id,
        String title,
        String description,
        String location,
        JobType jobType,
        WorkMode workMode,
        JobStatus status,
        ExperienceLevel experienceLevel,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        String currency,
        LocalDate deadline,
        String companyName,
        Boolean isVerified
) {}