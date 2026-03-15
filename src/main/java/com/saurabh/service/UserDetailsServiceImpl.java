package com.saurabh.service;

import lombok.RequiredArgsConstructor;

import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.saurabh.Entity.User;
import com.saurabh.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) {

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	    
	    return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),   // IMPORTANT
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
	}
}