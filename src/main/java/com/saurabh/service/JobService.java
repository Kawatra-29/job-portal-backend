package com.saurabh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

		Employer employer = employerRepository.findByUser_Email(email).orElseThrow();

		Job job = Job.builder().employer(employer).title(request.title()).description(request.description())
				.requirements(request.requirements()).responsibilities(request.responsibilities())
				.jobType(request.jobType()).workMode(request.workMode()).location(request.location())
				.salaryMin(request.salaryMin()).experienceLevel(request.experienceLevel()).deadline(request.deadline())
				.status(JobStatus.OPEN) // important
				.build();

		logger.info("Creating new job with title: {}", job.getTitle());

		return jobRepository.save(job);
	}

	public Optional<Job> getJob(@NonNull Long id) {

		logger.debug("Fetching job with id {}", id);

		Job job = jobRepository.findById(id).orElseThrow(() -> {

			logger.error("Job not found with id {}", id);
			return new RuntimeException("Job not found");
		});

		return Optional.of(job);
	}

	public void updatejob(@NonNull Long id, JobRequestDto request, UserDetails userDetails) {

		String email = userDetails.getUsername();

		Employer employer = employerRepository.findByUser_Email(email).orElseThrow();

		Job job = jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));

		// security check
		if (!job.getEmployer().getId().equals(employer.getId())) {
			throw new RuntimeException("You cannot update this job");
		}

		job.setTitle(request.title());
		job.setDescription(request.description());
		job.setDeadline(request.deadline());
		job.setLocation(request.location());
		job.setRequirements(request.requirements());

		jobRepository.save(job);
	}

	public void deleteJob(@NonNull Long id, UserDetails userDetails) {
		String email = userDetails.getUsername();

		Employer employer = employerRepository.findByUser_Email(email).orElseThrow();

		Job job = jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));

		// security check
		if (!job.getEmployer().getId().equals(employer.getId())) {
			throw new RuntimeException("You cannot delete this job");
		}
		jobRepository.delete(job);
	}
	
	public void updateStatus(@NonNull Long id, JobStatus status, UserDetails userDetails) {
	    Employer employer = employerRepository.findByUser_Email(userDetails.getUsername()).orElseThrow();
	    Job job = jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
	    if (!job.getEmployer().getId().equals(employer.getId())) {
	        throw new RuntimeException("Unauthorized");
	    }
	    job.setStatus(status);
	    jobRepository.save(job);
	}

}
