package com.saurabh.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saurabh.DTOs.PasswordRequestDto;
import com.saurabh.DTOs.UserResponseDTO;
import com.saurabh.DTOs.UserUpdateRequestDto;
import com.saurabh.Entity.User;
import com.saurabh.exception.ResourceNotFoundException;
import com.saurabh.repository.UserRepository;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getProfile(UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole() != null ? user.getRole().name() : null);
    }

    @Transactional
    public UserResponseDTO updateUser(UserUpdateRequestDto request, UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user1 = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (request.phone() != null) user1.setPhone(request.phone());
        if (request.fullName() != null) user1.setFullName(request.fullName());
        User saved = userRepository.save(user1);
        return new UserResponseDTO(
                saved.getId(),
                saved.getFullName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getRole() != null ? saved.getRole().name() : null);
    }

    @Transactional
    public User updatePass(PasswordRequestDto password, UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(password.oldPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(password.newPassword()));
        return userRepository.save(user);
    }

    @Transactional
    @SuppressWarnings("null")
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(u -> new UserResponseDTO(
                        u.getId(),
                        u.getFullName(),
                        u.getEmail(),
                        u.getPhone(),
                        u.getRole() != null ? u.getRole().name() : null));
    }
}
