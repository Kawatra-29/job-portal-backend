package com.saurabh.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.saurabh.DTOs.EmployerRequestDto;
import com.saurabh.Entity.Application;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.Job;
import com.saurabh.service.EmployerService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/employer")
public class EmployerController {

	@Autowired
	private EmployerService employerService;

	
	@GetMapping("/me")
	@PreAuthorize("hasRole('EMPLOYER')")
	@SecurityRequirement(name = "bearerAuth")
	public Employer getProfile(@AuthenticationPrincipal UserDetails userDetails)
	{
		return employerService.getProfile(userDetails);
	}
	
	
	@PutMapping("/me")
	@PreAuthorize("hasRole('EMPLOYER')")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> update(@RequestBody EmployerRequestDto employerRequestDto,@AuthenticationPrincipal UserDetails userDetails)
	{
		Employer employer = employerService.update(employerRequestDto, userDetails);

	    return ResponseEntity.ok(employer);
		
	}
	
	
	@GetMapping("/jobs")
	@PreAuthorize("hasRole('EMPLOYER')")
	@SecurityRequirement(name = "bearerAuth")
	public List<Job> getMyJobs(@AuthenticationPrincipal UserDetails userDetails) {
		return employerService.getMyJobs(userDetails);
	}
	
	@GetMapping("/jobs/{jobId}/applications")
	@PreAuthorize("hasRole('EMPLOYER')")
	@SecurityRequirement(name = "bearerAuth")
	public List<Application> getApplications(@PathVariable Long jobId) {

	    return employerService.getApplications(jobId);
	}
	
	
	
	
	
}
//
//| `GET` | `/employers/me` | Get own company profile | Employer |
//| `PUT` | `/employers/me` | Update company info (name, description, etc.) | Employer |
//| `POST` | `/employers/me/logo` | Upload company logo (multipart/form-data) | Employer |
//| `GET` | `/employers/{id}` | Get public employer/company profile | Public |
//| `GET` | `/employers/{id}/reviews` | List reviews for a company | Public |
//| `POST` | `/admin/employers/{id}/verify` | Grant verified badge to company | Admin |
