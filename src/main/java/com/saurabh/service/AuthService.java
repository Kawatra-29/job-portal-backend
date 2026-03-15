package com.saurabh.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saurabh.DTOs.AuthRequestDto;
import com.saurabh.DTOs.AuthResponseDto;
import com.saurabh.DTOs.LoginDto;
import com.saurabh.ENUMS.AuthStatus;
import com.saurabh.ENUMS.Role;
import com.saurabh.Entity.JobSeeker;
import com.saurabh.Entity.User;
import com.saurabh.repository.JobseekerRepository;
import com.saurabh.repository.UserRepository;
import com.saurabh.util.JwtUtils;
import jakarta.transaction.Transactional;

@Service
public class AuthService {

	@Autowired
    private JobseekerRepository jobseekerRepository;
	private PasswordEncoder passwordEncoder;
	private UserRepository userRepository;
	private AuthenticationManager authenticationManager;
	

	public AuthService(PasswordEncoder passwordEncoder,
			UserRepository userRepository, AuthenticationManager authenticationManager, JobseekerRepository jobseekerRepository, UserDetailsServiceImpl userDetailsServiceImpl) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.jobseekerRepository = jobseekerRepository;
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
	}

	@Transactional
	public AuthResponseDto Login(LoginDto loginDto) {

		var authToken = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());
		
		var authenticate = authenticationManager.authenticate(authToken);
		
		
		UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
		
		User user = userRepository.findByEmail(userDetails.getUsername())
		        .orElseThrow();

		String token = JwtUtils.generateToken(user);

		return new AuthResponseDto(token, AuthStatus.LOGIN_SUCCESS);
	}

	@Transactional // all database operation either success or rollback remain operations if any
					// one operation is unsuccessfull
	public AuthResponseDto signup(AuthRequestDto request) {

		// 1️⃣ Check if user already exists
		if (userRepository.existsByEmail(request.email())) {
			return new AuthResponseDto(null, AuthStatus.USER_ALREADY_EXISTS);
		}
		
		if (request.role() == Role.ADMIN) {
		    throw new RuntimeException("You cannot register as ADMIN");
		}

		// 2️⃣ Decide Role
		Role assignedRole;
		

		if (request.role() == Role.EMPLOYER) {
			assignedRole = Role.EMPLOYER;
		} else {
			assignedRole = Role.JOBSEEKER; // default
		}

		// 3️⃣ Create User
		User user = User.builder().fullName(request.fname()).email(request.email()).createdAt(LocalDateTime.now()).phone(request.phone()).updatedAt(LocalDateTime.now())
				.passwordHash(passwordEncoder.encode(request.password())).role(assignedRole).build();
		
		userRepository.save(user);

		// 4️⃣ If Candidate → Create Candidate Profile
		if (assignedRole == Role.JOBSEEKER) {
			
			JobSeeker jobSeekerProfile = JobSeeker.builder()
					.user(user)
					.build();
			
			jobseekerRepository.save(jobSeekerProfile);
		}
		else{
			// EMPLOYER 
			
		}
		// 5 Generate Token
		String token = JwtUtils.generateToken(user);

		return new AuthResponseDto(token, AuthStatus.USER_CREATED_SUCCESSFULLY);
	}
}
