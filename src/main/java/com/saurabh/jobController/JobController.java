package com.saurabh.jobController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saurabh.entity.Job;
import com.saurabh.service.JobService;

@RestController
@RequestMapping("/jobs")
public class JobController {
	@Autowired // to inject jobservice bean into this class(DI)
	private JobService jobService;

	@GetMapping("/all")
	public List<Job> getAllJobs() {
		return jobService.getAllJobs();
	}

	@GetMapping("/{type}")
	public Job getJobById(@PathVariable String type) { /// VARIABLE coming from URL PATH
		return jobService.getJob(type);
	}

}
