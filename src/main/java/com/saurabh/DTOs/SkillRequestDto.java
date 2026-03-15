package com.saurabh.DTOs;

import com.saurabh.ENUMS.ProficiencyLevel;

public record SkillRequestDto(
        Long skillId,
        ProficiencyLevel level
) {}