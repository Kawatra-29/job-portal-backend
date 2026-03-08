package com.saurabh.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.saurabh.entity.User;



public interface UserRepository extends JpaRepository<User, Long>{
		boolean existsByEmail(String email);
		
		boolean existsByPassword(String password);
		
		Optional<User> findByEmail(String email);
}
