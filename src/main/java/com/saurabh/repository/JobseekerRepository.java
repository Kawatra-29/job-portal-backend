package com.saurabh.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.Entity.JobSeeker;
import com.saurabh.Entity.User;

//import java.util.List;


public interface JobseekerRepository extends JpaRepository<JobSeeker, Long> {
	
	Optional<JobSeeker> findByUser(User user);

	JobSeeker findByUser_Email(String email);
}
