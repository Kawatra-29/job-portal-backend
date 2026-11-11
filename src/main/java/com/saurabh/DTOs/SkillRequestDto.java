package com.saurabh.DTOs;

import com.saurabh.ENUMS.ProficiencyLevel;
import jakarta.validation.constraints.NotNull;

public record SkillRequestDto(
		@NotNull(message = "Skill ID is required")
		Long skillId,

		@NotNull(message = "Proficiency level is required")
		ProficiencyLevel level
) {}