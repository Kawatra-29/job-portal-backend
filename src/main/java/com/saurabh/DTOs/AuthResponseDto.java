package com.saurabh.DTOs;

import com.saurabh.ENUMS.AuthStatus;
import com.saurabh.ENUMS.Role;

public record AuthResponseDto(String token, AuthStatus authStatus,Role role) {
	
}