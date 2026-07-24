package com.saurabh.DTOs;

public record ResetPasswordDto(String email, String otp, String newPassword) {
}
