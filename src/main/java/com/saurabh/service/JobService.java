package com.saurabh.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.saurabh.entity.Job;
import com.saurabh.repository.JobRepository;

//spring business service

@Service
public class JobService {
	@Autowired
	private JobRepository jobRepository;

	private static final Logger logger = LoggerFactory.getLogger(JobService.class);

	public Job createJob(Job job) {

		logger.info("Creating new job with title: {}", job.getTitle());

		return jobRepository.save(job);
	}
	
	
	public Job getJob(Long id){

	    logger.debug("Fetching job with id {}", id);

	    Job job = jobRepository.findById(id)
	            .orElseThrow(() -> {

	                logger.error("Job not found with id {}", id);
	                return new RuntimeException("Job not found");
	            });

	    return job;
	}
}
