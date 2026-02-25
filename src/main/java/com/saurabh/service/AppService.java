package com.saurabh.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.saurabh.entity.Application;
import com.saurabh.entity.ApplyDTO;
import com.saurabh.entity.Job;
import com.saurabh.entity.Candidate;
import com.saurabh.repository.AppRepository;
import com.saurabh.repository.candidateRepository;
import com.saurabh.repository.jobRepository;

@Service
public class AppService {
	
	@Autowired
	private AppRepository appRepository;
	
	@Autowired
	private candidateRepository candidateRepository;
	
	@Autowired
	private jobRepository jobRepository;
	
	private Application application = new Application();
	
	public void apply(ApplyDTO app)
	{
		application.setDate(app.getDate());
		application.setStatus(app.getStatus());
		Candidate candidate = candidateRepository
		        .findById(app.getCandidate_id())
		        .orElseThrow(() -> new RuntimeException("Candidate not found"));
		application.setCandidate(candidate);
		Job job = jobRepository.findById(app.getJob_id())
				.orElseThrow(() -> new RuntimeException("Candidate not found"));
		application.setJob(job);
		
		appRepository.save(application);
	}
	
	public Optional<Application> getAppById(int id)
	{
		return appRepository.findById(id);
	}
}
