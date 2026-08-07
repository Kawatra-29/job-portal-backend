package com.saurabh.Controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.saurabh.DTOs.AuthRequestDto;
import com.saurabh.DTOs.AuthResponseDto;
import com.saurabh.DTOs.LoginDto;
import com.saurabh.DTOs.ResetPasswordDto;
import com.saurabh.ENUMS.AuthStatus;
import com.saurabh.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;
	private final com.saurabh.util.JwtUtils jwtUtils;

	public AuthController(AuthService authService, com.saurabh.util.JwtUtils jwtUtils) {
		this.authService = authService;
		this.jwtUtils = jwtUtils;
	}

	private ResponseCookie createAuthCookie(String token, long maxAgeSeconds) {
		return ResponseCookie.from("authToken", token != null ? token : "")
				.httpOnly(true)
				.secure(false)
				.path("/")
				.maxAge(maxAgeSeconds)
				.sameSite("Lax")
				.build();
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> Login(@RequestBody LoginDto loginDto) {
		var authResponseDto = authService.Login(loginDto);
		if (authResponseDto.token() != null && !authResponseDto.token().isEmpty()) {
			ResponseCookie cookie = createAuthCookie(authResponseDto.token(), 24 * 60 * 60);
			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, cookie.toString())
					.body(authResponseDto);
		}
		return ResponseEntity.ok(authResponseDto);
	}

	@PostMapping("/sign-up")
	public ResponseEntity<AuthResponseDto> signUp(@RequestBody AuthRequestDto authRequestDto) {
		var authResponseDto = authService.signup(authRequestDto);

		if (authResponseDto.authStatus() == AuthStatus.USER_ALREADY_EXISTS) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(authResponseDto);
		}

		return ResponseEntity.status(HttpStatus.CREATED).body(authResponseDto);
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<AuthResponseDto> verifyOtp(
			@org.springframework.web.bind.annotation.RequestParam String email,
			@org.springframework.web.bind.annotation.RequestParam String otp) {
		var response = authService.verifyOtp(email, otp);
		if (response.authStatus() == AuthStatus.INVALID_OR_EXPIRED_OTP) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (response.token() != null && !response.token().isEmpty()) {
			ResponseCookie cookie = createAuthCookie(response.token(), 24 * 60 * 60);
			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, cookie.toString())
					.body(response);
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/resend-otp")
	public ResponseEntity<AuthResponseDto> resendOtp(
			@org.springframework.web.bind.annotation.RequestParam String email) {
		var response = authService.resendOtp(email);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<AuthResponseDto> forgotPassword(
			@org.springframework.web.bind.annotation.RequestParam String email) {
		var response = authService.forgotPassword(email);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/reset-password")
	public ResponseEntity<AuthResponseDto> resetPassword(
			@RequestBody ResetPasswordDto resetPasswordDto) {
		var response = authService.resetPassword(resetPasswordDto);
		if (response.authStatus() == AuthStatus.INVALID_OR_EXPIRED_OTP) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		if (response.token() != null && !response.token().isEmpty()) {
			ResponseCookie cookie = createAuthCookie(response.token(), 24 * 60 * 60);
			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, cookie.toString())
					.body(response);
		}
		return ResponseEntity.ok(response);
	}

	@PostMapping("/logout")
	public ResponseEntity<AuthResponseDto> logout(jakarta.servlet.http.HttpServletRequest request) {
		String token = null;
		if (request.getCookies() != null) {
			for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
				if ("authToken".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
					token = cookie.getValue();
					break;
				}
			}
		}
		if (token == null) {
			String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
				token = authHeader.substring(7);
			}
		}
		if (token != null) {
			jwtUtils.blacklistToken(token);
		}

		ResponseCookie cookie = createAuthCookie("", 0);
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(new AuthResponseDto(null, AuthStatus.LOGOUT_SUCCESS, null));
	}
}






















//| `POST` | `/auth/register` | Register new user (seeker or employer) | Public |
//| `POST` | `/auth/login` | Login and receive JWT access + refresh tokens | Public |
//| `POST` | `/auth/refresh` | Exchange refresh token for new access token | Public |
//| `POST` | `/auth/logout` | Invalidate refresh token | User |
//| `POST` | `/auth/forgot-password` | Send password reset email | Public |
//| `POST` | `/auth/reset-password` | Reset password using emailed token | Public |
//| `GET` | `/auth/verify-email` | Verify email address via token link | Public |
