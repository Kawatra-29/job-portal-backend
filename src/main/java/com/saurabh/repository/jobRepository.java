package com.saurabh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.entity.Job;

public interface jobRepository extends JpaRepository<Job, Integer>{

	
}
