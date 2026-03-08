package com.saurabh.DTOs;

import com.saurabh.ENUMS.Role;

public record AuthRequestDto(String name, String email, String password,Role role) {
}
// record used to only hold data (and reduce boilerplate code like constructors,getters etc..)
