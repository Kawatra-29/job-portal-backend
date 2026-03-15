package com.saurabh.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.saurabh.ENUMS.ExperienceLevel;
import com.saurabh.ENUMS.JobType;
import com.saurabh.ENUMS.WorkMode;

public record JobRequestDto(String title, String description, String requirements, String responsibilities,
		JobType jobType, WorkMode workMode, String location, BigDecimal salaryMin, BigDecimal salaryMax,
		String currency, ExperienceLevel experienceLevel, LocalDate deadline) {

}
