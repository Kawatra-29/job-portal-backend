package com.saurabh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.saurabh.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	
	 // FIX 4: Return our own User entity instead of Spring's User wrapper.
    //
    // Before: this method wrapped our User into a new Spring Security User object.
    // That broke AuthService.Login() because casting the principal to our User entity
    // would throw ClassCastException (it was a Spring User, not our User).
    //
    // Our User entity already implements UserDetails (see User.java), so we can
    // return it directly. This means:
    //   1. AuthService can safely cast: (User) authenticate.getPrincipal()
    //   2. We avoid building a redundant wrapper object on every request
    //   3. Any custom fields on User are accessible from the SecurityContext
	
	
	private final UserRepository userRepository;
		 @Override
		    public UserDetails loadUserByUsername(String email) {
		        return userRepository.findByEmail(email)
		                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
		    }
		
//	    User user = userRepository.findByEmail(email)
//	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//	    
//	    return new org.springframework.security.core.userdetails.User(
//                user.getEmail(),
//                user.getPasswordHash(),   // IMPORTANT
//                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
//        );
	
}
