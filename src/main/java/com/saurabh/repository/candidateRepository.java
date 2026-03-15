package com.saurabh.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.Entity.Candidate;
import com.saurabh.Entity.User;


public interface candidateRepository extends JpaRepository<Candidate, Integer> {

	Optional<Candidate> findByUser(User user);
	 
}
