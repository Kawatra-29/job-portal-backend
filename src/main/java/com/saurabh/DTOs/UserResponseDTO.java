package com.saurabh.DTOs;

public record UserResponseDTO(

        Long id,
        String fname,
        String email,
        String phone,
        String role

) {}