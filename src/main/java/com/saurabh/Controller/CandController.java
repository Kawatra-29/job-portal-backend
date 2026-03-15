package com.saurabh.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saurabh.Entity.Candidate;
import com.saurabh.Entity.User;
import com.saurabh.repository.candidateRepository;
import com.saurabh.service.candidateService;

@RestController
@RequestMapping("/api/candidate")
public class CandController {

	@Autowired
	private candidateRepository candidateRepository;
	@Autowired // to inject jobservice bean into this class(DI)
	private candidateService candidateService;
	
	@PreAuthorize("hasRole('CANDIDATE')")
	@GetMapping("/profile")
	public User getProfile(@AuthenticationPrincipal User user) {
	    return user;
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/all")
	public List<Candidate> getallCandidates() {
		return candidateRepository.findAll();
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{id}")
	public void getJobByID(@PathVariable int id) {
		candidateService.getCandidateById(id);
	}
	
//	@PreAuthorize("hasRole('ADMIN')")
//	@GetMapping("/{id}")
//	public void deleteJob(@PathVariable int id) {
//		candidateService.deleteCandidate(id);
//	}
//	
	
	@PutMapping("/profile")
	public ResponseEntity<Candidate> update(@RequestBody Candidate candidate) {

	    Candidate updated = candidateService.update(candidate);

	    return ResponseEntity.ok(updated);
	}
}

//CANDIDATE APIs — /api/candidate
//11. GET /api/candidate/profile — Get logged-in candidate's own profile (CANDIDATE) 
//12. PUT /api/candidate/profile — Update candidate profile (CANDIDATE)
//13. GET /api/candidate/all — Get all candidates (ADMIN)
//14. GET /api/candidate/{id} — Get candidate by ID (ADMIN)
//15. DELETE /api/candidate/{id} — Delete candidate (ADMIN)