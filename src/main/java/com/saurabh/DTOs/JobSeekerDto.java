package com.saurabh.DTOs;

import java.math.BigDecimal;
import java.util.Set;

import com.saurabh.ENUMS.Availability;

public record JobSeekerDto(String headline,String summary,String location,Integer yearsOfExperience,BigDecimal expectedSalary,Availability availability,Set<SkillRequestDto> skills) {

}
