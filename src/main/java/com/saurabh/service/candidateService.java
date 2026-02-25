package com.saurabh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.saurabh.DTOs.RegisterCandidateDTO;
import com.saurabh.repository.candidateRepository;
import com.saurabh.entity.Candidate;
import com.saurabh.exception.EmailAlreadyExistsException;
import com.saurabh.mapperpackage.CandidateMapper;


@Service
public class candidateService {
	@Autowired
	private candidateRepository candidateRepository;
	@Autowired
	private CandidateMapper mapper ;
	
	public void register(RegisterCandidateDTO dto) {
		Candidate candidate = mapper.toEntity(dto);
		  if (candidateRepository.existsByEmail(dto.getEmail())) {
		        throw new EmailAlreadyExistsException("Email already registered");
		    }
		candidateRepository.save(candidate);
	}
	
	public void deleteCandidate(int id)
	{
		candidateRepository.deleteById(id);
	}
}
