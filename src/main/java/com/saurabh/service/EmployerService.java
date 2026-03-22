package com.saurabh.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.saurabh.DTOs.EmployerRequestDto;
import com.saurabh.Entity.Application;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.Job;
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
	
	
	public List<Job> getMyJobs(UserDetails userDetails) {

	    String email = userDetails.getUsername();

	    Employer employer = employerRepository.findByUser_Email(email).orElseThrow();

	    return jobRepository.findByEmployerId(employer.getId());
	}
	
	
	public List<Application> getApplications(Long jobId) {

	    return applicationRepository.findByJobId(jobId);
	}


	public Employer getProfile(UserDetails userDetails) {
		String email = userDetails.getUsername();
		
	    return employerRepository.findByUser_Email(email)
	            .orElseThrow(() -> new RuntimeException("Employer not found"));
		
	}


	public Employer update(EmployerRequestDto request, UserDetails userDetails) {

	    String email = userDetails.getUsername();

	    Employer employer = employerRepository
	            .findByUser_Email(email)
	            .orElseThrow(() -> new RuntimeException("Employer not found"));

	    employer.setCompanyName(request.companyName());
	    employer.setDescription(request.description());
	    employer.setCompanySize(request.companySize());
	    employer.setIsVerified(false);

	    return employerRepository.save(employer);
	}


	public void verifyEmployer(Long id) {
	    Employer employer = employerRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Employer not found"));
	    
	    employer.setIsVerified(true);
	    employerRepository.save(employer);
	}
}
