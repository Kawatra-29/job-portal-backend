package com.saurabh.jobController;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.saurabh.entity.candidate;
import com.saurabh.service.candidateService;


@RestController
@RequestMapping("/candidate")
public class CandController {
	@Autowired // to inject jobservice bean into this class(DI)
	private candidateService candidateService;

	@GetMapping("/{id}")
	public Optional<candidate> getJobById(@PathVariable int id) { /// VARIABLE coming from URL PATH
		return candidateService.getCand(id); 
	}
	
	@PostMapping("/add")
	public void addJob(@RequestBody candidate cand)
	{
		candidateService.addCandidate(cand);
	}
	
	@GetMapping("/delete/{id}")
	public void deleteJob(@PathVariable int id) {
		candidateService.deleteCandidate(id);
	}
}
