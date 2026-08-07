package com.saurabh.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.saurabh.DTOs.JobRequestDto;
import com.saurabh.DTOs.JobResponseDto;
import com.saurabh.ENUMS.JobStatus;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.Job;
import com.saurabh.exception.ResourceNotFoundException;
import com.saurabh.mapper.JobMapper;
import com.saurabh.repository.EmployerRepository;
import com.saurabh.repository.JobRepository;

//spring business service

@Service
public class JobService {
	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private EmployerRepository employerRepository;

	@Autowired
	private JobMapper jobMapper;

	@Autowired(required = false)
	private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

	private static final Logger logger = LoggerFactory.getLogger(JobService.class);
	private static final int MAX_JOBS_PER_HOUR = 10;
	private static final String JOB_POST_LIMIT_PREFIX = "rate_limit:job_post:";
	private static final String JOB_VIEWS_PREFIX = "job:views:";

	@CacheEvict(value = {"jobs", "jobs_list"}, allEntries = true)
	@Transactional
	public Job createJob(JobRequestDto request, UserDetails userDetails) {

		String email = userDetails.getUsername();

		Employer employer = employerRepository.findByUser_Email(email)
				.orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

		// Rate limiting check: Max 10 job posts per hour per employer
		if (redisTemplate != null) {
			try {
				String key = JOB_POST_LIMIT_PREFIX + employer.getId();
				Long count = redisTemplate.opsForValue().increment(key);
				if (count != null && count == 1) {
					redisTemplate.expire(key, 1, java.util.concurrent.TimeUnit.HOURS);
				}
				if (count != null && count > MAX_JOBS_PER_HOUR) {
					throw new IllegalStateException("Employer job posting limit exceeded (Max " + MAX_JOBS_PER_HOUR + " jobs per hour)");
				}
			} catch (IllegalStateException e) {
				throw e;
			} catch (Exception e) {
				logger.warn("Redis rate limit check skipped: {}", e.getMessage());
			}
		}

		Job job = Job.builder().employer(employer).title(request.title()).description(request.description())
				.requirements(request.requirements()).responsibilities(request.responsibilities())
				.jobType(request.jobType()).workMode(request.workMode()).location(request.location())
				.salaryMin(request.salaryMin()).salaryMax(request.salaryMax()).currency(request.currency())
				.experienceLevel(request.experienceLevel()).deadline(request.deadline()).status(JobStatus.OPEN).build();

		logger.info("Creating new job with title: {}", job.getTitle());

		return jobRepository.save(job);
	}

	@Cacheable(value = "jobs", key = "#id")
	@Transactional(readOnly = true)
	public JobResponseDto getJob(Long id) {

		Job job = jobRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

		// Increment Redis view counter
		if (redisTemplate != null) {
			try {
				redisTemplate.opsForValue().increment(JOB_VIEWS_PREFIX + id);
			} catch (Exception e) {
				logger.warn("Redis view count increment failed for job {}: {}", id, e.getMessage());
			}
		}

		return jobMapper.toDTO(job);
	}

	@CacheEvict(value = {"jobs", "jobs_list"}, allEntries = true)
	@Transactional
	public void updatejob(@NonNull Long id, JobRequestDto request, UserDetails userDetails) {

		String email = userDetails.getUsername();

		Employer employer = employerRepository.findByUser_Email(email)
				.orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

		Job job = jobRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

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

	@CacheEvict(value = {"jobs", "jobs_list"}, allEntries = true)
	@Transactional
	public void deleteJob(@NonNull Long id, UserDetails userDetails) {
		String email = userDetails.getUsername();

		Employer employer = employerRepository.findByUser_Email(email)
				.orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

		Job job = jobRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));

		if (!job.getEmployer().getId().equals(employer.getId())) {
			throw new org.springframework.security.access.AccessDeniedException("You cannot delete this job");
		}
		jobRepository.delete(job);
	}

	@CacheEvict(value = {"jobs", "jobs_list"}, allEntries = true)
	@Transactional
	public void updateStatus(@NonNull Long id, JobStatus status, UserDetails userDetails) {
		Employer employer = employerRepository.findByUser_Email(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
		Job job = jobRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
		if (!job.getEmployer().getId().equals(employer.getId())) {
			throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
		}
		job.setStatus(status);
		jobRepository.save(job);
	}

}
