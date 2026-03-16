package com.saurabh.service;

//import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.saurabh.DTOs.PasswordRequestDto;
import com.saurabh.DTOs.UserResponseDTO;
import com.saurabh.Entity.User;
import com.saurabh.repository.UserRepository;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;

    UserService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

	
    public UserResponseDTO getProfile(UserDetails userDetails) {

        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole().name()
        );
    }

	public User updateUser(User user, UserDetails userDetails) {
		String email = userDetails.getUsername();

		User user1 = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
	    
	    user1.setPhone(user.getPhone());
	    user1.setFullName(user.getFullName());
	    
		return userRepository.save(user1);
	}
	
	public User updatePass(PasswordRequestDto password, UserDetails userDetails) {
		 String email = userDetails.getUsername();

	      User user = userRepository.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("User not found"));
		
	      // check current password
	        if (!passwordEncoder.matches(password.oldPassword(), user.getPassword())) {
	            throw new RuntimeException("Current password is incorrect");
	        }

	        // set new password
	        user.setPasswordHash(passwordEncoder.encode(password.newPassword()));

	         return userRepository.save(user);
		
	}


	public void deleteUser(String email) {

	      User user = userRepository.findByEmail(email)
	                .orElseThrow(() -> new RuntimeException("User not found"));

		  userRepository.delete(user);
	}


	public Page<User> getAllUsers(int page, int size) {
		 Pageable pageable = PageRequest.of(page, size);

		    return userRepository.findAll(pageable);
	}
	
	
	
}
