package com.saurabh.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saurabh.Entity.Application;
import com.saurabh.Entity.Job;
import com.saurabh.service.EmployerService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/employer")
public class EmployerController {

	@Autowired
	private EmployerService employerService;

	@GetMapping("/jobs")
	@PreAuthorize("hasRole('EMPLOYER')")
	@SecurityRequirement(name = "bearerAuth")
	public List<Job> getMyJobs(@AuthenticationPrincipal UserDetails userDetails) {
		return employerService.getMyJobs(userDetails);
	}
	
	@GetMapping("/jobs/{jobId}/applications")
	public List<Application> getApplications(@PathVariable Long jobId) {

	    return employerService.getApplications(jobId);
	}
}

//EMPLOYER APIs — /api/employer
//21. GET /api/employer/jobs — Get all jobs posted by logged-in employer (EMPLOYER)
//22. GET /api/employer/jobs/{jobId}/applicants — Get all applicants for a specific job (EMPLOYER)