package com.saurabh.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.saurabh.ENUMS.ExperienceLevel;
import com.saurabh.ENUMS.JobType;
import com.saurabh.ENUMS.WorkMode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record JobRequestDto(
		@NotBlank(message = "Job title is required")
		String title,

		@NotBlank(message = "Job description is required")
		String description,

		String requirements,
		String responsibilities,

		@NotNull(message = "Job type is required")
		JobType jobType,

		@NotNull(message = "Work mode is required")
		WorkMode workMode,

		@NotBlank(message = "Location is required")
		String location,

		@PositiveOrZero(message = "Minimum salary cannot be negative")
		BigDecimal salaryMin,

		@PositiveOrZero(message = "Maximum salary cannot be negative")
		BigDecimal salaryMax,

		String currency,

		@NotNull(message = "Experience level is required")
		ExperienceLevel experienceLevel,

		LocalDate deadline
) {}
