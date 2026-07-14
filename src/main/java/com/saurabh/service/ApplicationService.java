package com.saurabh.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.saurabh.ENUMS.ApplicationStatus;
import com.saurabh.Entity.Application;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.Job;
import com.saurabh.Entity.JobSeeker;
import com.saurabh.Entity.User;
import com.saurabh.DTOs.MyApplicationResponseDto;
import com.saurabh.mapper.JobMapper;
import com.saurabh.exception.ResourceNotFoundException;
import com.saurabh.repository.ApplicationRepository;
import com.saurabh.repository.EmployerRepository;
import com.saurabh.repository.JobRepository;
import com.saurabh.repository.JobseekerRepository;
import com.saurabh.repository.UserRepository;

@Service
public class ApplicationService {

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JobseekerRepository jobseekerRepository;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private EmployerRepository employerRepository;

	@Autowired
	private JobMapper jobMapper;

	@SuppressWarnings("null")
	@Transactional
	public void applyJob(@NonNull Long jobId, UserDetails userDetails) {
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		JobSeeker jobSeeker = jobseekerRepository.findByUser(user)
				.orElseThrow(() -> new ResourceNotFoundException("JobSeeker profile not found"));

		if (applicationRepository.existsByJobAndJobSeeker(job, jobSeeker)) {
			throw new IllegalStateException("You have already applied for this job");
		}
		Application application = Application.builder()
				.job(job)
				.jobSeeker(jobSeeker)
				.appliedAt(java.time.LocalDateTime.now())
				.status(com.saurabh.ENUMS.ApplicationStatus.APPLIED)
				.build();

		applicationRepository.save(application);
	}

	@Transactional
	public void updateStatus(@NonNull Long applicationId, ApplicationStatus newStatus, UserDetails userDetails) {
		Application application = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

		Employer employer = employerRepository.findByUser_Email(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

		if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
			throw new org.springframework.security.access.AccessDeniedException(
					"You are not authorized to update this application");
		}

		application.setStatus(newStatus);
		applicationRepository.save(application);
	}

	@Transactional(readOnly = true)
	public List<MyApplicationResponseDto> getMyApplications(UserDetails userDetails) {
		Optional<JobSeeker> jobSeekerOpt = Optional.ofNullable(jobseekerRepository.findByUser_Email(userDetails.getUsername()));
		List<Application> apps = jobSeekerOpt.map(applicationRepository::findByJobSeeker)
				.orElseThrow(() -> new ResourceNotFoundException("JobSeeker profile not found"));
		return apps.stream()
				.map(app -> new MyApplicationResponseDto(
						app.getId(),
						jobMapper.toDTO(app.getJob()),
						app.getStatus(),
						app.getAppliedAt()
				))
				.collect(Collectors.toList());
	}

	@Transactional
	public void withdraw(@NonNull Long applicationId, UserDetails userDetails) {
		Application application = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

		JobSeeker jobSeeker = jobseekerRepository.findByUser_Email(userDetails.getUsername());
		if (jobSeeker == null) throw new ResourceNotFoundException("JobSeeker profile not found");

		if (!application.getJobSeeker().getId().equals(jobSeeker.getId())) {
			throw new org.springframework.security.access.AccessDeniedException(
					"You are not authorized to withdraw this application");
		}

		applicationRepository.delete(application);
	}
}
