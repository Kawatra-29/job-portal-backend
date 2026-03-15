package com.saurabh.configuration;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.saurabh.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;
	private final UserDetailsService userDetailsService;

	public JWTAuthenticationFilter(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// done this to remove authentication filter from open apis
		if (request.getServletPath().startsWith("/api/auth")) {
			filterChain.doFilter(request, response);
			return;
		}

		/// fetch token from the request
		var jwttokenoptional = getTokenFromRequest(request);

		// validate this token -> JWT UTILS
		jwttokenoptional.ifPresent(jwtToken -> {
			if (jwtUtils.validateToken(jwtToken)) {
				// get username from jwt token
				var username = jwtUtils.getUsernameFromToken(jwtToken);

				// fetch user details with the help of username
				var userDetails = userDetailsService.loadUserByUsername(username);

				// Create Authentication Token
				var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null,
						userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				// set Authentication token to security context
				// for Duplicate authentication avoid
				if (SecurityContextHolder.getContext().getAuthentication() == null) {
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}
		});

		// pass request annd response to next filter
		filterChain.doFilter(request, response);
	}

	private Optional<String> getTokenFromRequest(HttpServletRequest request) {
		// Extract Authen Header
		var authheader = request.getHeader(HttpHeaders.AUTHORIZATION);
		/// jwt token is coming in the form "BEARER <JWT TOKEN>"
		if (authheader != null && !authheader.isEmpty() && authheader.toLowerCase().startsWith("bearer ")) {
			return Optional.of(authheader.substring(7));
		}
		return Optional.empty();
	}

}
