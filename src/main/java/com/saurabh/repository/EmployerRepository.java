package com.saurabh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saurabh.Entity.Employer;

public interface EmployerRepository  extends JpaRepository<Employer, Long>{

	Employer findByEmail(String email);
	
    
}
