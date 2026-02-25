package com.saurabh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.entity.Candidate;

public interface candidateRepository extends JpaRepository<Candidate, Integer> {
	 boolean existsByEmail(String email);
}
