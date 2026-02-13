package com.saurabh.service;

import java.util.Optional;
import com.saurabh.entity.candidate;
import com.saurabh.repository.candidateRepository;

public class candidateService {
	
	private candidateRepository candidateRepository;
	
	public Optional<candidate> getCand(int id)
	{
	  return candidateRepository.findById(id);
	}
	
	public void addCandidate(candidate cand) {
		candidateRepository.save(cand);
	}
	
	public void deleteCandidate(int id)
	{
		candidateRepository.deleteById(id);
	}
}
