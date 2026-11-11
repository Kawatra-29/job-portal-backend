package com.saurabh.DTOs;

import java.math.BigDecimal;
import java.util.Set;

import com.saurabh.ENUMS.Availability;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record JobSeekerDto(
		@Size(max = 100, message = "Headline must be at most 100 characters")
		String headline,

		String summary,
		String location,

		@PositiveOrZero(message = "Years of experience cannot be negative")
		Integer yearsOfExperience,

		@PositiveOrZero(message = "Expected salary cannot be negative")
		BigDecimal expectedSalary,

		Availability availability,

		Set<@Valid SkillRequestDto> skills
) {}
