package com.saurabh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.saurabh.DTOs.JobRequestDto;
import com.saurabh.ENUMS.JobStatus;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.Job;
import com.saurabh.exception.ResourceNotFoundException;
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

	@Transactional
	public Job createJob(JobRequestDto request, UserDetails userDetails) {

		String email = userDetails.getUsername();

		Employer employer = employerRepository.findByUser_Email(email).orElseThrow(
				() -> new ResourceNotFoundException("Employer not found"));

		Job job = Job.builder().employer(employer).title(request.title()).description(request.description())
				.requirements(request.requirements()).responsibilities(request.responsibilities())
				.jobType(request.jobType()).workMode(request.workMode()).location(request.location())
				.salaryMin(request.salaryMin()).salaryMax(request.salaryMax()).currency(request.currency())
				.experienceLevel(request.experienceLevel()).deadline(request.deadline())
				.status(JobStatus.OPEN)
				.build();

		logger.info("Creating new job with title: {}", job.getTitle());

		return jobRepository.save(job);
	}

	@Transactional(readOnly = true)
	public Job getJob(@NonNull Long id) {

		logger.debug("Fetching job with id {}", id);

		Job job = jobRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Job not found with id: " + id));

		return job;
	}

	@Transactional
	public void updatejob(@NonNull Long id, JobRequestDto request, UserDetails userDetails) {

		String email = userDetails.getUsername();

		Employer employer = employerRepository.findByUser_Email(email).orElseThrow(
				() -> new ResourceNotFoundException("Employer not found"));

		Job job = jobRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Job not found with id: " + id));

		if (!job.getEmployer().getId().equals(employer.getId())) {
			throw new org.springframework.security.access.AccessDeniedException("You cannot update this job");
		}

		job.setTitle(request.title());
		job.setDescription(request.description());
		job.setRequirements(request.requirements());
		job.setResponsibilities(request.responsibilities());
		job.setDeadline(request.deadline());
		job.setLocation(request.location());
		job.setJobType(request.jobType());
		job.setWorkMode(request.workMode());
		job.setSalaryMin(request.salaryMin());
		job.setSalaryMax(request.salaryMax());
		job.setCurrency(request.currency());
		job.setExperienceLevel(request.experienceLevel());

		jobRepository.save(job);
	}

	@Transactional
	public void deleteJob(@NonNull Long id, UserDetails userDetails) {
		String email = userDetails.getUsername();

		Employer employer = employerRepository.findByUser_Email(email).orElseThrow(
				() -> new ResourceNotFoundException("Employer not found"));

		Job job = jobRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Job not found with id: " + id));

		if (!job.getEmployer().getId().equals(employer.getId())) {
			throw new org.springframework.security.access.AccessDeniedException("You cannot delete this job");
		}
		jobRepository.delete(job);
	}

	@Transactional
	public void updateStatus(@NonNull Long id, JobStatus status, UserDetails userDetails) {
		Employer employer = employerRepository.findByUser_Email(userDetails.getUsername()).orElseThrow(
				() -> new ResourceNotFoundException("Employer not found"));
		Job job = jobRepository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Job not found with id: " + id));
		if (!job.getEmployer().getId().equals(employer.getId())) {
			throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
		}
		job.setStatus(status);
		jobRepository.save(job);
	}

}
