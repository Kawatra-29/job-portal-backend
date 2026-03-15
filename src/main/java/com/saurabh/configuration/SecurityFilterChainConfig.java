package com.saurabh.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableMethodSecurity
@Configuration
public class SecurityFilterChainConfig {

	private final AuthenticationEntryPoint authenticationEntryPoint;

	private final JWTAuthenticationFilter jwtAuthenticationfilter;

	public SecurityFilterChainConfig(AuthenticationEntryPoint authenticationEntryPoint,
			JWTAuthenticationFilter jwtAuthenticationFilter) {
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.jwtAuthenticationfilter = jwtAuthenticationFilter;
	}

	@Bean
	SecurityFilterChain SecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
		// desable cors
		httpSecurity.cors(CorsConfig -> CorsConfig.disable());
		// disable csrf
		httpSecurity.csrf(CorsConfig -> CorsConfig.disable());

		httpSecurity.authorizeHttpRequests(auth -> auth
		        .requestMatchers("/api/auth/**").permitAll()
		        .requestMatchers("/h2-console/**").permitAll()
		        .requestMatchers(
		                "/swagger-ui/**",
		                "/v3/api-docs/**",
		                "/swagger-ui.html"
		        ).permitAll()

		        .requestMatchers("/api/admin/**").hasRole("ADMIN")
		        .requestMatchers("/api/employer/**").hasRole("EMPLOYER")
		        .requestMatchers("/api/candidate/**").hasRole("CANDIDATE")

		        .anyRequest().authenticated()
		);
		
		httpSecurity.headers(headers -> headers.frameOptions(frame -> frame.disable()));
		

		// Authentication entry point -> exception hndler

		httpSecurity.exceptionHandling(
				exceptionConfig -> exceptionConfig.authenticationEntryPoint(authenticationEntryPoint));
		httpSecurity.sessionManagement(
				SessionConfig -> SessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		
		
		
		/// ADD JWT AUTHENTICATION FILTER
		httpSecurity.addFilterBefore(jwtAuthenticationfilter, UsernamePasswordAuthenticationFilter.class);
		

		return httpSecurity.build();
	}

}
