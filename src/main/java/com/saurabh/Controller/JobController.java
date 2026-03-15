package com.saurabh.Controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saurabh.DTOs.JobRequestDto;
import com.saurabh.Entity.Job;
import com.saurabh.repository.JobRepository;
import com.saurabh.service.JobService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
public class JobController {

	// @Autowired // to inject jobservice bean into this class(DI)
	// private JobService jobService;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	public JobService jobService;

	@GetMapping("/jobs")
	public Page<Job> getJobs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);

		return jobRepository.findAll(pageable);
	}

	@GetMapping("/jobs/{id}")
	public Optional<Job> getJobsById(@PathVariable long id) {

		return jobRepository.findById(id);
	}

	@PostMapping("/jobs")
	@PreAuthorize("hasRole('EMPLOYER')")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<Job> create(@RequestBody JobRequestDto jobRequestDto,
			@AuthenticationPrincipal UserDetails userDetails) {
		Job job = jobService.createJob(jobRequestDto, userDetails);

		return ResponseEntity.ok(job);

	}
}

//| `GET` | `/jobs` | Search and list jobs (filters, pagination, sort) | Public |
//| `GET` | `/jobs/{id}` | Get job details (increments `views_count`) | Public |
//| `POST` | `/jobs` | Create a new job listing | Employer |
//| `PUT` | `/jobs/{id}` | Update job listing | Employer (owner) |
//| `DELETE` | `/jobs/{id}` | Delete a job listing | Employer (owner) |
//| `PATCH` | `/jobs/{id}/status` | Change status: ACTIVE / PAUSED / CLOSED | Employer (owner) |
//| `GET` | `/employers/me/jobs` | List own job postings with stats | Employer |
//| `GET` | `/jobs/recommended` | Get skill-matched job recommendations | Jobseeker |
