package com.saurabh.Entity;

import java.time.LocalDateTime;

import com.saurabh.ENUMS.ApplicationStatus;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applications", indexes = {
		// 1. Duplicate application 
		@Index(name = "idx_app_job_seeker", columnList = "job_id, job_seeker_id"),

		// 2.  all applicants fetch of Particular job 
		@Index(name = "idx_app_job_id", columnList = "job_id"),

		// 3. applied jobs list 
		@Index(name = "idx_app_seeker_id", columnList = "job_seeker_id") })

public class Application {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "job_seeker_id")
	private JobSeeker jobSeeker;

	@ManyToOne
	@JoinColumn(name = "job_id")
	private Job job;

	private LocalDateTime appliedAt;

	@Enumerated(EnumType.STRING)
	private ApplicationStatus status; /// (APPLIED, SHORTLISTED, REJECTED etc....)

}
