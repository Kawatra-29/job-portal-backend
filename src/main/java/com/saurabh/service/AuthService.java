package com.saurabh.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.saurabh.entity.User;
import com.saurabh.DTOs.AuthRequestDto;
import com.saurabh.DTOs.AuthResponseDto;
import com.saurabh.ENUMS.AuthStatus;
import com.saurabh.ENUMS.Role;
import com.saurabh.entity.Candidate;
import com.saurabh.repository.UserRepository;
import com.saurabh.repository.candidateRepository;
import com.saurabh.util.JwtUtils;
import jakarta.transaction.Transactional;

@Service
public class AuthService {

	private PasswordEncoder passwordEncoder;
	private candidateRepository candidateRepository;
	private UserRepository userRepository;
	private AuthenticationManager authenticationManager;
	
	

	public AuthService(PasswordEncoder passwordEncoder, com.saurabh.repository.candidateRepository candidateRepository,
			UserRepository userRepository, AuthenticationManager authenticationManager) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.candidateRepository = candidateRepository;
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
	}

	@Transactional
	public AuthResponseDto Login(AuthRequestDto authRequestDto) {

		var authToken = new UsernamePasswordAuthenticationToken(authRequestDto.email(), authRequestDto.password());
		
		var authenticate = authenticationManager.authenticate(authToken);
		
		var user = (User) authenticate.getPrincipal();

		String token = JwtUtils.generateToken(user);

		return new AuthResponseDto(token, AuthStatus.LOGIN_SUCCESS);
	}

	@Transactional // all database operation either success or rollback remain operations if any
					// one operation is unsuccessfull
	public AuthResponseDto signup(AuthRequestDto request) {

		System.out.println("REQUEST PAHUCH GYI SERVICE TAK .......");
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
			assignedRole = Role.CANDIDATE; // default
		}

		// 3️⃣ Create User
		User user = User.builder().name(request.name()).email(request.email())
				.password(passwordEncoder.encode(request.password())).role(assignedRole).build();

		
		userRepository.save(user);
		
		System.out.println("USER SAVED .......");

		// 4️⃣ If Candidate → Create Candidate Profile
		if (assignedRole == Role.CANDIDATE) {
			Candidate candidate = Candidate.builder().user(user).experience("0").build();

			candidateRepository.save(candidate);
		}

		// 5 Generate Token
		String token = JwtUtils.generateToken(user);

		return new AuthResponseDto(token, AuthStatus.USER_CREATED_SUCCESSFULLY);
	}
}
