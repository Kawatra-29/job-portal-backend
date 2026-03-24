package com.saurabh.repository;

import java.util.List;
//import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;

import com.saurabh.Entity.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

	@SuppressWarnings("null")
	public Optional<Job> findById(Long id);
	
	List<Job> findByEmployerId(Long employerId);
	
	// to solve N + 1 QUERY PROBLEM 
//	@Query("""
//			SELECT j FROM Job j
//			LEFT JOIN FETCH j.applications
//			WHERE j.title = :title
//			""")
//	List<Job> findJobsWithApplications(String title);

}
