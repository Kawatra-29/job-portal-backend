package com.saurabh.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.saurabh.DTOs.AuthRequestDto;
import com.saurabh.DTOs.AuthResponseDto;
import com.saurabh.DTOs.LoginDto;
import com.saurabh.DTOs.ResetPasswordDto;
import com.saurabh.ENUMS.AuthStatus;
import com.saurabh.ENUMS.Role;
import com.saurabh.Entity.Employer;
import com.saurabh.Entity.JobSeeker;
import com.saurabh.Entity.OtpVerification;
import com.saurabh.Entity.PasswordResetToken;
import com.saurabh.Entity.User;
import com.saurabh.repository.EmployerRepository;
import com.saurabh.repository.JobseekerRepository;
import com.saurabh.repository.OtpRepository;
import com.saurabh.repository.PasswordResetRepository;
import com.saurabh.repository.UserRepository;
import com.saurabh.util.JwtUtils;
import jakarta.transaction.Transactional;

@Service
public class AuthService {

	private JobseekerRepository jobseekerRepository;
	private EmployerRepository employerRepository;
	private JwtUtils jwtUtils;
	private PasswordEncoder passwordEncoder;
	private UserRepository userRepository;
	private AuthenticationManager authenticationManager;

	@Autowired
	private OtpRepository otpRepository;

	@Autowired
	private PasswordResetRepository passwordResetRepository;

	@Autowired
	private EmailService emailService;

	private final Random random = new SecureRandom();

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
		String normalizedEmail = loginDto.email() != null ? loginDto.email().trim().toLowerCase() : "";
		var authToken = new UsernamePasswordAuthenticationToken(normalizedEmail, loginDto.password());
		var authenticate = authenticationManager.authenticate(authToken);

		User user = (User) authenticate.getPrincipal();

		if (user.getIsEmailVerified() != null && !user.getIsEmailVerified()) {
			resendOtp(user.getEmail());
			return new AuthResponseDto(null, AuthStatus.EMAIL_NOT_VERIFIED, user.getRole());
		}

		String token = jwtUtils.generateToken(user);
		return new AuthResponseDto(token, AuthStatus.LOGIN_SUCCESS, user.getRole());
	}

	@SuppressWarnings("null")
	@Transactional
	public AuthResponseDto signup(AuthRequestDto request) {
		String normalizedEmail = request.email() != null ? request.email().trim().toLowerCase() : "";

		// 1️⃣ Check if user already exists
		if (userRepository.existsByEmail(normalizedEmail)) {
			return new AuthResponseDto(null, AuthStatus.USER_ALREADY_EXISTS, null);
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

		// 3️⃣ Create User (isEmailVerified = false)
		User user = User.builder()
				.fullName(request.fname())
				.email(normalizedEmail)
				.createdAt(LocalDateTime.now())
				.phone(request.phone())
				.updatedAt(LocalDateTime.now())
				.passwordHash(passwordEncoder.encode(request.password()))
				.role(assignedRole)
				.isEmailVerified(false)
				.build();

		userRepository.save(user);

		if (assignedRole == Role.JOBSEEKER) {
			JobSeeker jobSeeker = JobSeeker.builder().user(user).build();
			jobseekerRepository.save(jobSeeker);
		} else {
			Employer employer = Employer.builder().user(user).build();
			employerRepository.save(employer);
		}

		// 4️⃣ Generate 6-Digit OTP
		String otp = String.format("%06d", random.nextInt(1000000));

		OtpVerification otpRecord = otpRepository.findByEmail(normalizedEmail)
				.orElse(new OtpVerification());
		otpRecord.setEmail(normalizedEmail);
		otpRecord.setOtp(otp);
		otpRecord.setExpiryTime(LocalDateTime.now().plusMinutes(5));
		otpRepository.save(otpRecord);

		// 5️⃣ Send Email
		emailService.sendOtpEmail(normalizedEmail, otp);

		return new AuthResponseDto(null, AuthStatus.USER_CREATED_SUCCESSFULLY, user.getRole());
	}

	@Transactional
	public AuthResponseDto verifyOtp(String email, String inputOtp) {
		String normalizedEmail = email != null ? email.trim().toLowerCase() : "";
		String trimmedOtp = inputOtp != null ? inputOtp.trim() : "";

		OtpVerification otpRecord = otpRepository.findByEmail(normalizedEmail)
				.orElse(null);

		if (otpRecord == null || LocalDateTime.now().isAfter(otpRecord.getExpiryTime()) || !otpRecord.getOtp().equals(trimmedOtp)) {
			return new AuthResponseDto(null, AuthStatus.INVALID_OR_EXPIRED_OTP, null);
		}

		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		user.setIsEmailVerified(true);
		userRepository.save(user);

		otpRepository.delete(otpRecord);

		String token = jwtUtils.generateToken(user);
		return new AuthResponseDto(token, AuthStatus.OTP_VERIFIED_SUCCESSFULLY, user.getRole());
	}

	@Transactional
	public AuthResponseDto resendOtp(String email) {
		String normalizedEmail = email != null ? email.trim().toLowerCase() : "";
		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
			return new AuthResponseDto(null, AuthStatus.LOGIN_SUCCESS, user.getRole());
		}

		String otp = String.format("%06d", random.nextInt(1000000));

		OtpVerification otpRecord = otpRepository.findByEmail(normalizedEmail)
				.orElse(new OtpVerification());
		otpRecord.setEmail(normalizedEmail);
		otpRecord.setOtp(otp);
		otpRecord.setExpiryTime(LocalDateTime.now().plusMinutes(5));
		otpRepository.save(otpRecord);

		emailService.sendOtpEmail(normalizedEmail, otp);

		return new AuthResponseDto(null, AuthStatus.OTP_SENT_SUCCESSFULLY, user.getRole());
	}

	@Transactional
	public AuthResponseDto forgotPassword(String email) {
		String normalizedEmail = email != null ? email.trim().toLowerCase() : "";
		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found with this email address"));

		String otp = String.format("%06d", random.nextInt(1000000));

		PasswordResetToken resetToken = passwordResetRepository.findByEmail(normalizedEmail)
				.orElse(new PasswordResetToken());
		resetToken.setEmail(normalizedEmail);
		resetToken.setOtp(otp);
		resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(15));
		passwordResetRepository.save(resetToken);

		emailService.sendPasswordResetEmail(normalizedEmail, otp);

		return new AuthResponseDto(null, AuthStatus.OTP_SENT_SUCCESSFULLY, user.getRole());
	}

	@Transactional
	public AuthResponseDto resetPassword(ResetPasswordDto request) {
		String normalizedEmail = request.email() != null ? request.email().trim().toLowerCase() : "";
		String trimmedOtp = request.otp() != null ? request.otp().trim() : "";

		PasswordResetToken resetToken = passwordResetRepository.findByEmail(normalizedEmail)
				.orElse(null);

		if (resetToken == null || LocalDateTime.now().isAfter(resetToken.getExpiryTime()) || !resetToken.getOtp().equals(trimmedOtp)) {
			return new AuthResponseDto(null, AuthStatus.INVALID_OR_EXPIRED_OTP, null);
		}

		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
		userRepository.save(user);

		passwordResetRepository.delete(resetToken);

		return new AuthResponseDto(null, AuthStatus.PASSWORD_RESET_SUCCESSFULLY, user.getRole());
	}
}
