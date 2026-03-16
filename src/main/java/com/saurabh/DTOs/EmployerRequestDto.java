package com.saurabh.DTOs;

import com.saurabh.ENUMS.CompanySize;


public record EmployerRequestDto(String companyName,CompanySize companySize,String description) {

}
