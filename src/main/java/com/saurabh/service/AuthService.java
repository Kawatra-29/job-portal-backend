package com.saurabh.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.saurabh.DTOs.AuthRequestDto;
import com.saurabh.DTOs.AuthResponseDto;
import com.saurabh.DTOs.LoginDto;
import com.saurabh.ENUMS.AuthStatus;
import com.saurabh.ENUMS.Role;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.JobSeeker;
import com.saurabh.Entity.User;
import com.saurabh.repository.EmployerRepository;
import com.saurabh.repository.JobseekerRepository;
import com.saurabh.repository.UserRepository;
import com.saurabh.util.JwtUtils;
import jakarta.transaction.Transactional;

@Service
public class AuthService {

	@Autowired
	private JobseekerRepository jobseekerRepository;
	@Autowired
	private EmployerRepository employerRepository;

	@Autowired
	private JwtUtils jwtUtils;
	private PasswordEncoder passwordEncoder;
	private UserRepository userRepository;
	private AuthenticationManager authenticationManager;

	public AuthService(JobseekerRepository jobseekerRepository, EmployerRepository employerRepository,
			JwtUtils jwtUtils, PasswordEncoder passwordEncoder, UserRepository userRepository,
			AuthenticationManager authenticationManager) {
		super();
		this.jobseekerRepository = jobseekerRepository;
		this.employerRepository = employerRepository;
		this.jwtUtils = jwtUtils;
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
	}

	@Transactional
	public AuthResponseDto Login(LoginDto loginDto) {

		var authToken = new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password());

		var authenticate = authenticationManager.authenticate(authToken);

		UserDetails userDetails = (UserDetails) authenticate.getPrincipal();

		User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

		String token = jwtUtils.generateToken(user);

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
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
		}

		// 2️⃣ Decide Role
		Role assignedRole;

		if (request.role() == Role.EMPLOYER) {
			assignedRole = Role.EMPLOYER;
		} else {
			assignedRole = Role.JOBSEEKER; // default
		}

		// 3️⃣ Create User
		User user = User.builder().fullName(request.fname()).email(request.email()).createdAt(LocalDateTime.now())
				.phone(request.phone()).updatedAt(LocalDateTime.now())
				.passwordHash(passwordEncoder.encode(request.password())).role(assignedRole).build();

		userRepository.save(user);

		// 4️⃣ If Candidate → Create Candidate Profile
		if (assignedRole == Role.JOBSEEKER) {

			JobSeeker jobSeeker = JobSeeker.builder().user(user).build();

			jobseekerRepository.save(jobSeeker);
		} else {
			// EMPLOYER
			Employer employer = Employer.builder().user(user).build();
			employerRepository.save(employer);

		}
		// 5 Generate Token
		String token = jwtUtils.generateToken(user);

		return new AuthResponseDto(token, AuthStatus.USER_CREATED_SUCCESSFULLY);
	}
}
