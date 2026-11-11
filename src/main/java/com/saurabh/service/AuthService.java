package com.saurabh.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
	private static final String OTP_PREFIX = "OTP:";
	private static final String RESET_PREFIX = "RESET:";

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

	@Autowired(required = false)
	private StringRedisTemplate redisTemplate;

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

		// 4️⃣ Generate 6-Digit OTP & Store in Redis with 5 min TTL
		String otp = String.format("%06d", random.nextInt(1000000));
		saveOtpWithTtl(normalizedEmail, otp, 5);

		// 5️⃣ Send Email
		emailService.sendOtpEmail(normalizedEmail, otp);

		return new AuthResponseDto(null, AuthStatus.USER_CREATED_SUCCESSFULLY, user.getRole());
	}

	@Transactional
	public AuthResponseDto verifyOtp(String email, String inputOtp) {
		String normalizedEmail = email != null ? email.trim().toLowerCase() : "";
		String trimmedOtp = inputOtp != null ? inputOtp.trim() : "";

		boolean isValid = validateAndClearOtp(normalizedEmail, trimmedOtp);

		if (!isValid) {
			return new AuthResponseDto(null, AuthStatus.INVALID_OR_EXPIRED_OTP, null);
		}

		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		user.setIsEmailVerified(true);
		userRepository.save(user);

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
		saveOtpWithTtl(normalizedEmail, otp, 5);

		emailService.sendOtpEmail(normalizedEmail, otp);

		return new AuthResponseDto(null, AuthStatus.OTP_SENT_SUCCESSFULLY, user.getRole());
	}

	@Transactional
	public AuthResponseDto forgotPassword(String email) {
		String normalizedEmail = email != null ? email.trim().toLowerCase() : "";
		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found with this email address"));

		String otp = String.format("%06d", random.nextInt(1000000));
		saveResetTokenWithTtl(normalizedEmail, otp, 15);

		emailService.sendPasswordResetEmail(normalizedEmail, otp);

		return new AuthResponseDto(null, AuthStatus.OTP_SENT_SUCCESSFULLY, user.getRole());
	}

	@Transactional
	public AuthResponseDto resetPassword(ResetPasswordDto request) {
		String normalizedEmail = request.email() != null ? request.email().trim().toLowerCase() : "";
		String trimmedOtp = request.otp() != null ? request.otp().trim() : "";

		boolean isValid = validateAndClearResetToken(normalizedEmail, trimmedOtp);

		if (!isValid) {
			return new AuthResponseDto(null, AuthStatus.INVALID_OR_EXPIRED_OTP, null);
		}

		User user = userRepository.findByEmail(normalizedEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
		userRepository.save(user);

		return new AuthResponseDto(null, AuthStatus.PASSWORD_RESET_SUCCESSFULLY, user.getRole());
	}
	@SuppressWarnings("null")
	private void saveOtpWithTtl(String email, String otp, int minutes) {
		try {
			if (redisTemplate != null) {
				redisTemplate.opsForValue().set(OTP_PREFIX + email, otp, minutes, TimeUnit.MINUTES);
				logger.info("Saved OTP to Redis for email: {} with TTL {} mins", email, minutes);
				return;
			}
		} catch (Exception e) {
			logger.warn("Redis unavailable for saving OTP, falling back to DB: {}", e.getMessage());
		}

		// Fallback to DB
		OtpVerification otpRecord = otpRepository.findByEmail(email).orElse(new OtpVerification());
		otpRecord.setEmail(email);
		otpRecord.setOtp(otp);
		otpRecord.setExpiryTime(LocalDateTime.now().plusMinutes(minutes));
		otpRepository.save(otpRecord);
	}

	private boolean validateAndClearOtp(String email, String inputOtp) {
		try {
			if (redisTemplate != null) {
				String cachedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + email);
				if (cachedOtp != null && cachedOtp.equals(inputOtp)) {
					redisTemplate.delete(OTP_PREFIX + email);
					return true;
				}
			}
		} catch (Exception e) {
			logger.warn("Redis unavailable for OTP validation, checking DB: {}", e.getMessage());
		}

		// Fallback check against DB
		OtpVerification otpRecord = otpRepository.findByEmail(email).orElse(null);
		if (otpRecord != null && !LocalDateTime.now().isAfter(otpRecord.getExpiryTime()) && otpRecord.getOtp().equals(inputOtp)) {
			otpRepository.delete(otpRecord);
			return true;
		}
		return false;
	}

	private void saveResetTokenWithTtl(String email, String otp, int minutes) {
		try {
			if (redisTemplate != null) {
				redisTemplate.opsForValue().set(RESET_PREFIX + email, otp, minutes, TimeUnit.MINUTES);
				logger.info("Saved Password Reset Token to Redis for email: {} with TTL {} mins", email, minutes);
				return;
			}
		} catch (Exception e) {
			logger.warn("Redis unavailable for saving Reset Token, falling back to DB: {}", e.getMessage());
		}

		PasswordResetToken resetToken = passwordResetRepository.findByEmail(email).orElse(new PasswordResetToken());
		resetToken.setEmail(email);
		resetToken.setOtp(otp);
		resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(minutes));
		passwordResetRepository.save(resetToken);
	}

	private boolean validateAndClearResetToken(String email, String inputOtp) {
		try {
			if (redisTemplate != null) {
				String cachedToken = redisTemplate.opsForValue().get(RESET_PREFIX + email);
				if (cachedToken != null && cachedToken.equals(inputOtp)) {
					redisTemplate.delete(RESET_PREFIX + email);
					return true;
				}
			}
		} catch (Exception e) {
			logger.warn("Redis unavailable for Reset Token validation, checking DB: {}", e.getMessage());
		}

		PasswordResetToken resetToken = passwordResetRepository.findByEmail(email).orElse(null);
		if (resetToken != null && !LocalDateTime.now().isAfter(resetToken.getExpiryTime()) && resetToken.getOtp().equals(inputOtp)) {
			passwordResetRepository.delete(resetToken);
			return true;
		}
		return false;
	}
}

