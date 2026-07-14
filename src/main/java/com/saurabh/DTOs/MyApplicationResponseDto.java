package com.saurabh.DTOs;

import java.time.LocalDateTime;
import com.saurabh.ENUMS.ApplicationStatus;

public record MyApplicationResponseDto(
    Long id,
    JobResponseDto job,
    ApplicationStatus status,
    LocalDateTime appliedAt
) {}
