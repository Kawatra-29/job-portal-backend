package com.saurabh.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.saurabh.service.ApplicationService;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@PostMapping("/{jobId}/apply")
	@PreAuthorize("hasRole('JOBSEEKER')")
	public ResponseEntity<?> applyJob(@PathVariable Long jobId, UserDetails userDetails) {

		applicationService.applyJob(jobId, userDetails);

		return ResponseEntity.ok("Applied successfully");
	}

//	@GetMapping("/{id}")
//	public Optional<Application> getAppById(@PathVariable int id) {
//		return appService.getAppById(id);
//	}
}

//| `POST` | `/jobs/{jobId}/apply` | Submit application for a job | Jobseeker |
//| `DELETE` | `/applications/{id}` | Withdraw own application | Jobseeker (owner) |
//| `GET` | `/jobseekers/me/applications` | List own applications with current status | Jobseeker |
//| `GET` | `/jobs/{jobId}/applications` | List all applicants for a job | Employer |
//| `GET` | `/applications/{id}` | Get full application details | Owner |
//| `PATCH` | `/applications/{id}/status` | Advance/decline application status | Employer |
//| `PUT` | `/applications/{id}/notes` | Add/update private notes on an application | Employer |