package com.saurabh.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import com.saurabh.ENUMS.ApplicationStatus;
import com.saurabh.Entity.Application;
import com.saurabh.service.ApplicationService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@PostMapping("/{jobId}/apply")
	@PreAuthorize("hasRole('JOBSEEKER')")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> applyJob(@PathVariable @NonNull Long jobId,@AuthenticationPrincipal UserDetails userDetails) {

		applicationService.applyJob(jobId, userDetails);

		return ResponseEntity.ok("Applied successfully");
	}
	
	// Employer: application status update
	@SuppressWarnings("null")
	@PatchMapping("/{id}/status")
	@PreAuthorize("hasRole('EMPLOYER')")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> updateStatus(
	        @PathVariable Long id,
	        @RequestParam ApplicationStatus status,
	        @AuthenticationPrincipal UserDetails userDetails) {
	    applicationService.updateStatus(id, status, userDetails);
	    return ResponseEntity.ok("Status updated");
	}

	// Jobseeker: apni applications dekho
	@GetMapping("/my")
	@PreAuthorize("hasRole('JOBSEEKER')")
	@SecurityRequirement(name = "bearerAuth")
	public List<Application> getMyApplications(@AuthenticationPrincipal UserDetails userDetails) {
	    return applicationService.getMyApplications(userDetails);
	}

	// Jobseeker: application withdraw
	@SuppressWarnings("null")
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('JOBSEEKER')")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> withdraw(@PathVariable Long id,
	        @AuthenticationPrincipal UserDetails userDetails) {
	    applicationService.withdraw(id, userDetails);
	    return ResponseEntity.noContent().build();
	}
}

//| `POST` | `/jobs/{jobId}/apply` | Submit application for a job | Jobseeker |
//| `DELETE` | `/applications/{id}` | Withdraw own application | Jobseeker (owner) |
//| `GET` | `/jobseekers/me/applications` | List own applications with current status | Jobseeker |
//| `GET` | `/jobs/{jobId}/applications` | List all applicants for a job | Employer |
//| `GET` | `/applications/{id}` | Get full application details | Owner |
//| `PATCH` | `/applications/{id}/status` | Advance/decline application status | Employer |
//| `PUT` | `/applications/{id}/notes` | Add/update private notes on an application | Employer |