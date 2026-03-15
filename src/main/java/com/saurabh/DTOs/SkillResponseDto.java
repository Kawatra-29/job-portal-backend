package com.saurabh.DTOs;

import com.saurabh.ENUMS.ProficiencyLevel;

public class SkillResponseDto {

    private String skillName;
    private ProficiencyLevel proficiencyLevel;

    public SkillResponseDto(String skillName, ProficiencyLevel proficiencyLevel) {
        this.skillName = skillName;
        this.proficiencyLevel = proficiencyLevel;
    }

    public String getSkillName() {
        return skillName;
    }

    public ProficiencyLevel getProficiencyLevel() {
        return proficiencyLevel;
    }
}