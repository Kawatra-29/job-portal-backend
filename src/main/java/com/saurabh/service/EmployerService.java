package com.saurabh.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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
	
	
	public List<Job> getMyJobs(@AuthenticationPrincipal UserDetails userDetails) {

	    String email = userDetails.getUsername();

	    Employer employer = employerRepository.findByEmail(email);

	    return jobRepository.findByEmployerId(employer.getId());
	}
	
	
	public List<Application> getApplications(@PathVariable Long jobId) {

	    return applicationRepository.findByJobId(jobId);
	}
}
