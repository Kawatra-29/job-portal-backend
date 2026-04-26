package com.saurabh.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import com.saurabh.DTOs.JobResponseDto;
import com.saurabh.Entity.Job;

@Mapper(componentModel = "spring")
public interface JobMapper {

	@Mapping(source = "employer.companyName", target = "companyName")
	@Mapping(source = "employer.isVerified", target = "isVerified")
    JobResponseDto toDTO(Job job);
	
	// Page mapping — MapStruct can't generate this, default method handles it
    default Page<JobResponseDto> __toDTO__(Page<Job> jobs) {
        return jobs.map(this::toDTO);
    }
}