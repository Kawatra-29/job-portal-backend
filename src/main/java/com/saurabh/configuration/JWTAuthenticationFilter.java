package com.saurabh.configuration;

import java.io.IOException;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
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
import jakarta.servlet.http.Cookie;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;
	private final UserDetailsService userDetailsService;

	public JWTAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
		this.jwtUtils = jwtUtils;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain)
			throws ServletException, IOException {

		// done this to remove authentication filter from open apis
		String path = request.getServletPath();

		if (path.startsWith("/api/v1/auth") ||
				path.startsWith("/v3/api-docs") ||
				path.startsWith("/swagger-ui") ||
				path.equals("/swagger-ui.html") ||
				path.startsWith("/h2-console")) {

			filterChain.doFilter(request, response);
			return;
		}

		/// fetch token from the request
		var jwttokenoptional = getTokenFromRequest(request);

		// validate this token -> JWT UTILS
		jwttokenoptional.ifPresent(jwtToken -> {
			if (!jwtUtils.isTokenBlacklisted(jwtToken) && jwtUtils.validateToken(jwtToken)) {
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
		// 1. Check cookies first
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("authToken".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isEmpty()) {
					return Optional.of(cookie.getValue());
				}
			}
		}

		// 2. Fallback to Authorization Header
		var authheader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authheader != null && !authheader.isEmpty() && authheader.toLowerCase().startsWith("bearer ")) {
			return Optional.of(authheader.substring(7));
		}
		return Optional.empty();
	}

}
