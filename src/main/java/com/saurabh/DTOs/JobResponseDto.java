package com.saurabh.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.saurabh.ENUMS.ExperienceLevel;
import com.saurabh.ENUMS.JobStatus;
import com.saurabh.ENUMS.JobType;
import com.saurabh.ENUMS.WorkMode;

@JsonIgnoreProperties(ignoreUnknown = true)
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
        Boolean isVerified,
        Long viewsCount
) {
}
