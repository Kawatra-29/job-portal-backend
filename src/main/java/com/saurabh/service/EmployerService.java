package com.saurabh.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saurabh.DTOs.EmployerRequestDto;
import com.saurabh.Entity.Application;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.Job;
import com.saurabh.exception.ResourceNotFoundException;
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
	
	@Transactional(readOnly = true)
	public List<Job> getMyJobs(UserDetails userDetails) {

	    String email = userDetails.getUsername();

	    Employer employer = employerRepository.findByUser_Email(email).orElseThrow();

	    return jobRepository.findByEmployerId(employer.getId());
	}
	
	@Transactional(readOnly = true)
	public List<Application> getApplications(Long jobId) {

	    return applicationRepository.findByJobId(jobId);
	}


	@Transactional(readOnly = true)
	public Employer getProfile(UserDetails userDetails) {
		String email = userDetails.getUsername();
		
	    return employerRepository.findByUser_Email(email)
	            .orElseThrow(() -> new RuntimeException("Employer not found"));
		
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
        // That meant admin had to re-verify after every single profile edit — terrible UX.
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
}
