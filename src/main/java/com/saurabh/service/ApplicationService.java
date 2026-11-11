package com.saurabh.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.saurabh.ENUMS.ApplicationStatus;
import com.saurabh.ENUMS.JobStatus;
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

	@Autowired(required = false)
	private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

	private static final int MAX_APPLICATIONS_PER_HOUR = 20;
	private static final String APPLICATION_LIMIT_PREFIX = "rate_limit:apply:";

	@SuppressWarnings("null")
	@Transactional
	public void applyJob(@NonNull Long jobId, UserDetails userDetails) {
		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		JobSeeker jobSeeker = jobseekerRepository.findByUser(user)
				.orElseThrow(() -> new ResourceNotFoundException("JobSeeker profile not found"));

		// Rate limiting check: Max 20 job applications per hour per candidate
		if (redisTemplate != null) {
			try {
				String key = APPLICATION_LIMIT_PREFIX + jobSeeker.getId();
				Long count = redisTemplate.opsForValue().increment(key);
				if (count != null && count == 1) {
					redisTemplate.expire(key, 1, java.util.concurrent.TimeUnit.HOURS);
				} else if (count != null && count > 1) {
					Long ttl = redisTemplate.getExpire(key);
					if (ttl != null && ttl < 0) {
						redisTemplate.expire(key, 1, java.util.concurrent.TimeUnit.HOURS);
					}
				}
				if (count != null && count > MAX_APPLICATIONS_PER_HOUR) {
					throw new IllegalStateException("Job application limit exceeded (Max " + MAX_APPLICATIONS_PER_HOUR
							+ " applications per hour)");
				}
			} catch (IllegalStateException e) {
				throw e;
			} catch (Exception e) {
				// Log & continue if Redis is offline
			}
		}

		if (applicationRepository.existsByJobAndJobSeeker(job, jobSeeker)) {
			throw new IllegalStateException("You have already applied for this job");
		}

		// Job OPEN honi chahiye — CLOSED, PAUSED, ya FILLED job pe apply nahi ho sakti
		if (job.getStatus() == null || job.getStatus() != JobStatus.OPEN) {
			String statusDesc = job.getStatus() != null ? job.getStatus().name().toLowerCase() : "unavailable";
			throw new IllegalStateException(
					"Cannot apply: this job is currently " + statusDesc + ".");
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
		JobSeeker jobSeeker = jobseekerRepository.findByUser_Email(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("JobSeeker profile not found"));
		List<Application> apps = applicationRepository.findByJobSeeker(jobSeeker);
		return apps.stream()
				.map(app -> new MyApplicationResponseDto(
						app.getId(),
						jobMapper.toDTO(app.getJob()),
						app.getStatus(),
						app.getAppliedAt()))
				.collect(Collectors.toList());
	}

	@Transactional
	public void withdraw(@NonNull Long applicationId, UserDetails userDetails) {
		Application application = applicationRepository.findById(applicationId)
				.orElseThrow(() -> new ResourceNotFoundException("Application not found: " + applicationId));

		JobSeeker jobSeeker = jobseekerRepository.findByUser_Email(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("JobSeeker profile not found"));

		if (!application.getJobSeeker().getId().equals(jobSeeker.getId())) {
			throw new org.springframework.security.access.AccessDeniedException(
					"You are not authorized to withdraw this application");
		}

		applicationRepository.delete(application);
	}
}
