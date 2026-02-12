package com.saurabh.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saurabh.entity.Job;
import com.saurabh.repository.jobRepository;

//spring business service

@Service
public class JobService {
	@Autowired
	private jobRepository jobRepo;

	public List<Job> getAllJobs() {
		List<Job> job = new ArrayList<>();
		 jobRepo.findAll().forEach(job::add);
		 return job;
	}

	public Optional<Job> getJob(int id)
	{
	    return jobRepo.findById(id);
	}
	
	public void addJob(Job job)
	{
		jobRepo.save(job);
	}
	
	public void deleteJob(int id)
	{
		jobRepo.deleteById(id);
	}
}
