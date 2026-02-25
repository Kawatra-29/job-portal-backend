package com.saurabh.Controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

	@GetMapping("/{id}")
	public Optional<Job> getJobById(@PathVariable int id) { /// VARIABLE coming from URL PATH
		return jobService.getJob(id);
	}
	
	@PostMapping("/add")
	public void addJob(@RequestBody Job job)
	{
		jobService.addJob(job);
	}
	
	@GetMapping("/delete/{id}")
	public void deleteJob(@PathVariable int id) {
		jobService.deleteJob(id);
		
	}

}
