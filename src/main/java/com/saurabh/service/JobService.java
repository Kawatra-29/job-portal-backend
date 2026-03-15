package com.saurabh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.saurabh.DTOs.JobRequestDto;
import com.saurabh.ENUMS.JobStatus;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.Job;
import com.saurabh.repository.EmployerRepository;
import com.saurabh.repository.JobRepository;

//spring business service

@Service
public class JobService {
	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private EmployerRepository employerRepository;

	private static final Logger logger = LoggerFactory.getLogger(JobService.class);

	public Job createJob(JobRequestDto request, UserDetails userDetails) {

		String email = userDetails.getUsername();

		Employer employer = employerRepository.findByEmail(email);
		
		Job job = Job.builder()
		        .employer(employer)
		        .title(request.title())
		        .description(request.description())
		        .requirements(request.requirements())
		        .responsibilities(request.responsibilities())
		        .jobType(request.jobType())
		        .workMode(request.workMode())
		        .location(request.location())
		        .salaryMin(request.salaryMin())
		        .experienceLevel(request.experienceLevel())
		        .deadline(request.deadline())
		        .status(JobStatus.OPEN)   // important
		        .build();

		logger.info("Creating new job with title: {}", job.getTitle());

		return jobRepository.save(job);
	}

	public Job getJob(Long id) {

		logger.debug("Fetching job with id {}", id);

		Job job = jobRepository.findById(id).orElseThrow(() -> {

			logger.error("Job not found with id {}", id);
			return new RuntimeException("Job not found");
		});

		return job;
	}
}
