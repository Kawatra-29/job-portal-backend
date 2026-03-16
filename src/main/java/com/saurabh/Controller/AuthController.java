package com.saurabh.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.saurabh.DTOs.AuthRequestDto;
import com.saurabh.DTOs.AuthResponseDto;
import com.saurabh.DTOs.LoginDto;
import com.saurabh.ENUMS.AuthStatus;
import com.saurabh.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> Login(@RequestBody LoginDto loginDto) {

		try {
			var authResponseDto = authService.Login(loginDto);

			return ResponseEntity.status(HttpStatus.OK).body(authResponseDto);
		} catch (Exception e) {
			e.printStackTrace();
			
			var authResponseDto = new AuthResponseDto(null, AuthStatus.LOGIN_FAILED);
			
			return ResponseEntity.status(HttpStatus.CONFLICT).body(authResponseDto);
		}
	}

	@PostMapping("/sign-up")
	public ResponseEntity<AuthResponseDto> signUp(@RequestBody AuthRequestDto authRequestDto) {
		try {

			var authResponseDto = authService.signup(authRequestDto);

			return ResponseEntity.status(HttpStatus.OK).body(authResponseDto);
		} catch (Exception e) {
			e.printStackTrace(); // IMPORTANT

			var authResponseDto = new AuthResponseDto(null, AuthStatus.USER_NOT_CREATED);

			return ResponseEntity.status(HttpStatus.CONFLICT).body(authResponseDto);
		}
	}
}






















//| `POST` | `/auth/register` | Register new user (seeker or employer) | Public |
//| `POST` | `/auth/login` | Login and receive JWT access + refresh tokens | Public |
//| `POST` | `/auth/refresh` | Exchange refresh token for new access token | Public |
//| `POST` | `/auth/logout` | Invalidate refresh token | User |
//| `POST` | `/auth/forgot-password` | Send password reset email | Public |
//| `POST` | `/auth/reset-password` | Reset password using emailed token | Public |
//| `GET` | `/auth/verify-email` | Verify email address via token link | Public |
