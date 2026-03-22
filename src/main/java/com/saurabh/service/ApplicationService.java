package com.saurabh.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saurabh.ENUMS.ApplicationStatus;
import com.saurabh.Entity.Application;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.Job;
import com.saurabh.Entity.JobSeeker;
import com.saurabh.Entity.User;
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

	@Transactional
	public void applyJob(Long jobId, UserDetails userDetails) {
		Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));

		User user = userRepository.findByEmail(userDetails.getUsername())
				.orElseThrow(() -> new RuntimeException("User not found"));

		JobSeeker jobSeeker = jobseekerRepository.findByUser(user)
				.orElseThrow(() -> new RuntimeException("Jobseeker profile not found"));

		if (applicationRepository.existsByJobAndJobSeeker(job, jobSeeker)) {
			throw new RuntimeException("You already applied for this job");
		}
		Application application = Application.builder().job(job).jobSeeker(jobSeeker).appliedAt(LocalDateTime.now())
				.build();

		applicationRepository.save(application);
	}
	
	// Application status update
	@Transactional
	public void updateStatus(Long applicationId, ApplicationStatus newStatus, UserDetails userDetails) {
	    Application application = applicationRepository.findById(applicationId)
	            .orElseThrow(() -> new RuntimeException("Application not found"));

	    // Verify employer owns this job
	    Employer employer = employerRepository.findByUser_Email(userDetails.getUsername())
	            .orElseThrow(() -> new RuntimeException("Employer not found"));

	    if (!application.getJob().getEmployer().getId().equals(employer.getId())) {
	        throw new RuntimeException("Unauthorized");
	    }
	    application.setStatus(newStatus);
	    applicationRepository.save(application);
	}

	// Jobseeker apni applications dekhe
	public List<Application> getMyApplications(UserDetails userDetails) {
	    JobSeeker jobSeeker = jobseekerRepository.findByUser_Email(userDetails.getUsername());
	    return applicationRepository.findByJobSeeker(jobSeeker);
	}

	// Application withdraw
	@Transactional
	public void withdraw(Long applicationId, UserDetails userDetails) {
	    Application application = applicationRepository.findById(applicationId)
	            .orElseThrow(() -> new RuntimeException("Application not found"));
	    JobSeeker jobSeeker = jobseekerRepository.findByUser_Email(userDetails.getUsername());
	    if (!application.getJobSeeker().getId().equals(jobSeeker.getId())) {
	        throw new RuntimeException("Unauthorized");
	    }
	    applicationRepository.delete(application);
	}
	
	
	
	
	
	
//	public Optional<Application> getAppById(int id)
//	{
//		return appRepository.findById(id);
//	}
}
