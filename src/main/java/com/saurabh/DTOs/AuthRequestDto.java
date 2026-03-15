package com.saurabh.DTOs;

import com.saurabh.ENUMS.Role;

public record AuthRequestDto(String fname, String email, String password,Role role,String phone) {
}
// record used to only hold data (and reduce boilerplate code like constructors,getters etc..)
