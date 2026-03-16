package com.saurabh.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import com.saurabh.Entity.User;
import com.saurabh.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/check")
	@SecurityRequirement(name = "bearerAuth")
	public String checkPrincipal(Authentication authentication) {
	    return authentication.getPrincipal().getClass().getName();
	}
	
	@GetMapping("/me")
	@PreAuthorize("hasRole('JOBSEEKER')")
	@SecurityRequirement(name = "bearerAuth")
	public UserResponseDTO getUser(@AuthenticationPrincipal UserDetails userDetails) {
	       return userService.getProfile(userDetails);
	}

	@PutMapping("/me")
	@SecurityRequirement(name = "bearerAuth")
	@PreAuthorize("hasRole('JOBSEEKER')")
    public ResponseEntity<User> updateUser(@RequestBody User user,@AuthenticationPrincipal UserDetails userDetails) {

        User updatedUser = userService.updateUser(user,userDetails);

        return ResponseEntity.ok(updatedUser);
    }
	
	@PutMapping("/password")
	@PreAuthorize("hasRole('JOBSEEKER')")
	@SecurityRequirement(name = "bearerAuth")
	public User updatePass(PasswordRequestDto password ,@AuthenticationPrincipal UserDetails userDetails) {
	       return userService.updatePass(password, userDetails);
	}
	
	
	@DeleteMapping("/me")
	@PreAuthorize("hasRole('JOBSEEKER')")
	@SecurityRequirement(name = "bearerAuth")
	public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {

	    userService.deleteUser(userDetails.getUsername());

	    return ResponseEntity.ok("User deleted successfully");
	}	
	
	
}

//| `GET` | `/users/me` | Get current user's profile | User |
//| `PUT` | `/users/me` | Update name, phone, profile picture | User |
//| `PUT` | `/users/me/password` | Change password (requires current password) | User |
//| `POST` | `/users/me/avatar` | Upload profile picture (multipart/form-data) | User |
//| `DELETE` | `/users/me` | Permanently delete own account | User |
//| `GET` | `/admin/users` | List all users with pagination | Admin |
//| `DELETE` | `/admin/users/{id}` | Ban or delete a user | Admin |