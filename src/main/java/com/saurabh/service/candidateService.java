package com.saurabh.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.saurabh.Entity.Candidate;
import com.saurabh.Entity.User;
import com.saurabh.repository.candidateRepository;


@Service
public class candidateService {
	@Autowired
	private candidateRepository candidateRepository;

	
	public void deleteCandidate(int id)
	{
		candidateRepository.deleteById(id);
	}


	public Optional<Candidate> getCandidateById(int id) {
		return candidateRepository.findById(id);
		
	}
	
	public Candidate update(Candidate candidate) {

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    var user = (User) auth.getPrincipal();

	    Candidate existingCandidate = candidateRepository.findByUser(user)
	            .orElseThrow(() -> new RuntimeException("Candidate not found"));

	    existingCandidate.setSkills(candidate.getSkills());
	    existingCandidate.setExperience(candidate.getExperience());

	    return candidateRepository.save(existingCandidate);
	}
}
