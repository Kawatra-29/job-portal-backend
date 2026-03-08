package com.saurabh.DTOs;

import com.saurabh.ENUMS.AuthStatus;

public record AuthResponseDto(String token, AuthStatus authStatus) {
	
}