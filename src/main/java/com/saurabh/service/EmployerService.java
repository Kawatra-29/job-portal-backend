package com.saurabh.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saurabh.DTOs.ApplicationResponseDto;
import com.saurabh.DTOs.EmployerRequestDto;
import com.saurabh.DTOs.EmployerSummaryDto;
import com.saurabh.DTOs.JobResponseDto;
import com.saurabh.DTOs.JobSeekerResponseDTO;
import com.saurabh.DTOs.SkillResponseDto;
import com.saurabh.DTOs.UserResponseDTO;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.JobSeeker;
import com.saurabh.Entity.Job;
import java.util.Collections;
import java.util.Set;
import com.saurabh.exception.ResourceNotFoundException;
import com.saurabh.mapper.JobMapper;
import com.saurabh.repository.ApplicationRepository;
import com.saurabh.repository.EmployerRepository;
import com.saurabh.repository.JobRepository;

@Service
public class EmployerService {
	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private EmployerRepository employerRepository;

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private JobMapper jobMapper;

	@Transactional(readOnly = true)
	public List<JobResponseDto> getMyJobs(UserDetails userDetails) {
		Employer employer = employerRepository.findByUser_Email(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
		return jobRepository.findByEmployerId(employer.getId())
				.stream().map(jobMapper::toDTO).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<ApplicationResponseDto> getApplications(@NonNull Long jobId, UserDetails userDetails) {
		Employer employer = employerRepository.findByUser_Email(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

		Job job = jobRepository.findById(jobId)
				.orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

		if (!job.getEmployer().getId().equals(employer.getId())) {
			throw new org.springframework.security.access.AccessDeniedException(
					"You are not authorized to view applications for this job");
		}

		return applicationRepository.findByJobId(jobId).stream()
				.map(a -> {
					JobSeeker profile = a.getJobSeeker();
					JobSeekerResponseDTO jobSeekerDto = null;
					if (profile != null) {
						Set<SkillResponseDto> skills = Optional.ofNullable(profile.getSkills())
								.orElse(Collections.emptySet())
								.stream()
								.map(jsSkill -> new SkillResponseDto(
										jsSkill.getSkill().getName(),
										jsSkill.getProficiencyLevel()))
								.collect(Collectors.toSet());

						var user = profile.getUser();
						UserResponseDTO userDto = user != null ? new UserResponseDTO(
								user.getId(),
								user.getFullName(),
								user.getEmail(),
								user.getPhone(),
								user.getRole().name()) : null;

						jobSeekerDto = new JobSeekerResponseDTO(
								profile.getId(),
								profile.getHeadline(),
								profile.getSummary(),
								profile.getLocation(),
								profile.getYearsOfExperience(),
								profile.getExpectedSalary(),
								profile.getAvailability() != null ? profile.getAvailability().name() : null,
								skills,
								userDto);
					}

					return new ApplicationResponseDto(
							a.getId(),
							a.getJob().getId(),
							a.getJob().getTitle(),
							a.getJob().getEmployer().getCompanyName(),
							a.getStatus(),
							a.getAppliedAt(),
							jobSeekerDto);
				}).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public EmployerSummaryDto getProfile(UserDetails userDetails) {
		Employer e = employerRepository.findByUser_Email(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("Employer not found"));
		return new EmployerSummaryDto(e.getId(), e.getCompanyName(), e.getCompanySize(), e.getDescription(),
				e.getIsVerified());
	}

	@Transactional
	public Employer update(EmployerRequestDto request, UserDetails userDetails) {
		Employer employer = employerRepository.findByUser_Email(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

		employer.setCompanyName(request.companyName());
		employer.setDescription(request.description());
		employer.setCompanySize(request.companySize());

		// FIX 7: Removed employer.setIsVerified(false) that was here before.
		//
		// Before: every time an employer updated their profile (even just changing
		// the description), their verification badge was reset to false.
		// That meant admin had to re-verify after every single profile edit — terrible
		// UX.
		//
		// Fix: don't touch isVerified at all during a normal profile update.
		// Verification is admin's job via POST /api/v1/admin/employers/{id}/verify.

		return employerRepository.save(employer);
	}

	@SuppressWarnings("null")
	@Transactional
	public void verifyEmployer(Long id) {
		Employer employer = employerRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Employer not found: " + id));
		employer.setIsVerified(true);
		employerRepository.save(employer);
	}

	@Transactional(readOnly = true)
	public List<ApplicationResponseDto> getAllApplications(UserDetails userDetails) {
		Employer employer = employerRepository.findByUser_Email(userDetails.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

		// Yeh already exist karta hai - ek hi query mein sab fetch
		List<Job> jobs = jobRepository.findByEmployerIdWithApplications(employer.getId());

		return jobs.stream()
				.flatMap(job -> job.getApplications().stream()
						.map(a -> new ApplicationResponseDto(
								a.getId(),
								job.getId(),
								job.getTitle(),
								employer.getCompanyName(),
								a.getStatus(),
								a.getAppliedAt(),
								null // jobSeekerDto add karo zaroorat ho to
						)))
				.collect(Collectors.toList());
	}

}
