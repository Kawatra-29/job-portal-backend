package com.saurabh.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.saurabh.DTOs.PasswordRequestDto;
import com.saurabh.DTOs.UserResponseDTO;
import com.saurabh.DTOs.UserUpdateRequestDto;
import com.saurabh.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/me")
	@SecurityRequirement(name = "bearerAuth")
	public UserResponseDTO getUser(@AuthenticationPrincipal UserDetails userDetails) {
		return userService.getProfile(userDetails);
	}

	@PutMapping("/me")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UserUpdateRequestDto request,
			@AuthenticationPrincipal UserDetails userDetails) {
		UserResponseDTO updatedUser = userService.updateUser(request, userDetails);
		return ResponseEntity.ok(updatedUser);
	}

	@PutMapping("/password")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<?> updatePass(@RequestBody @Valid PasswordRequestDto password,
			@AuthenticationPrincipal UserDetails userDetails) {
		userService.updatePass(password, userDetails);
		return ResponseEntity.ok(java.util.Map.of("message", "Password updated successfully"));
	}

	@DeleteMapping("/me")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
		userService.deleteUser(userDetails.getUsername());
		return ResponseEntity.ok("User deleted successfully");
	}

}
