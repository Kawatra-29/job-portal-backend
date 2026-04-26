package com.saurabh.DTOs;


import com.saurabh.ENUMS.CompanySize;

public record EmployerSummaryDto(
    Long id,
    String companyName,
    CompanySize companySize,
    String description,
    Boolean isVerified
) {}