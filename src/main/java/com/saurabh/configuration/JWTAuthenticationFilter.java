package com.saurabh.configuration;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpHeaders;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		/// fetch token from the request
		var jwttokenoptional = getTokenFromRequest(request);
		
		//validate this token -> JWT UTILS
		jwttokenoptional.ifPresent(jwtToken->{});
		
		
		

	}

	private Optional<String> getTokenFromRequest(HttpServletRequest request) {
		// Extract Authen Header
		var authheader = request.getHeader(HttpHeaders.AUTHORIZATION);
		/// jwt token is coming in the form "BEARER <JWT TOKEN>"
		if (authheader.isEmpty() && authheader.endsWith("bearer ")) {
			return Optional.of(authheader.substring(7));
		}
		return Optional.empty();
	}

}
