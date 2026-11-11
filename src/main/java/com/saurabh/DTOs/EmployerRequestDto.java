package com.saurabh.DTOs;

import com.saurabh.ENUMS.CompanySize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployerRequestDto(
		@NotBlank(message = "Company name is required")
		String companyName,

		@NotNull(message = "Company size is required")
		CompanySize companySize,

		String description
) {}
