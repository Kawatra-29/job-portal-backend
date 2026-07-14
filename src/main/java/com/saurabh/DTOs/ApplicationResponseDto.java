package com.saurabh.DTOs;

import java.time.LocalDateTime;

import com.saurabh.ENUMS.ApplicationStatus;

public record ApplicationResponseDto(
	    Long id,
	    Long jobId,
	    String jobTitle,
	    String companyName,
	    ApplicationStatus status,
	    LocalDateTime appliedAt,
	    JobSeekerResponseDTO jobSeeker
	) {}