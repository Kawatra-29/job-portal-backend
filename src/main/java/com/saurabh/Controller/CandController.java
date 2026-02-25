package com.saurabh.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.saurabh.DTOs.RegisterCandidateDTO;
import com.saurabh.entity.Candidate;
import com.saurabh.repository.candidateRepository;
import com.saurabh.service.candidateService;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/candidate")
public class CandController {

	@Autowired
    private candidateRepository candidateRepository;
	@Autowired // to inject jobservice bean into this class(DI)
	private candidateService candidateService;
	
	@GetMapping("/all")
	public List<Candidate> getallCandidates()
	{
		return candidateRepository.findAll();
	}
	
	@GetMapping("/delete/{id}")
	public void deleteJob(@PathVariable int id) {
		candidateService.deleteCandidate(id);
	}
	
	@PostMapping("/auth/register")
	public ResponseEntity<?> RegisterCandidate(@Valid @RequestBody RegisterCandidateDTO dto)
	{
		 candidateService.register(dto);
		 return ResponseEntity.status(HttpStatus.CREATED)
                 .body("Candidate Registered");
	}
}
