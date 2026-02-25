package com.saurabh.configuration;

import com.saurabh.GlobalExceptionHandler;

import org.apache.catalina.util.SessionConfig;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityFilterChainConfig {
	
	private final AuthenticationEntryPoint authenticationEntryPoint;
	
	private final JWTAuthenticationFilter jwtAuthenticationfilter;
	
	
	
	public  SecurityFilterChainConfig(AuthenticationEntryPoint authenticationEntryPoint) {
	
		this.authenticationEntryPoint = authenticationEntryPoint;
	}



	@Bean
	public SecurityFilterChain SecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		// desable cors  
		httpSecurity.cors(CorsConfig->CorsConfig.disable());
		// disable csrf
		httpSecurity.csrf(CorsConfig->CorsConfig.disable());
		
		httpSecurity.authorizeHttpRequests(
				RequestMatcher->
				RequestMatcher.requestMatchers("/auth/register").permitAll()
				.requestMatchers("/auth/register").permitAll()
				.anyRequest().authenticated()
				
				);
		
	    // Authentication  entry point -> exception hndler
		
		httpSecurity.exceptionHandling(exceptionConfig->exceptionConfig.authenticationEntryPoint(authenticationEntryPoint));
		httpSecurity.sessionManagement(SessionConfig->SessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		
		///ADD JWT AUTHENTICATION FILTER
		httpSecurity.addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class )
		
		return null;
	}
	
}
