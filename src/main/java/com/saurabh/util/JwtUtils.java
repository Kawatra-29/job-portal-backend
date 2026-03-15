package com.saurabh.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.saurabh.Entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
@Component
public class JwtUtils {

	private static final SecretKey secretKey = Jwts.SIG.HS256.key().build();
	private static final String ISSUER = "SAURABH KAWATRA";

	public boolean validateToken(String jwtToken) {
		return parseToken(jwtToken) != null;
	}

	private static Claims parseToken(String jwtToken) {
		var jwtParser = Jwts.parser().verifyWith(secretKey).build();

		try {
			return jwtParser.parseSignedClaims(jwtToken).getPayload();
		} catch (JwtException e) {
			System.err.println("Invalid JWT: " + e.getMessage());
		}
		return null;

	}

	public String getUsernameFromToken(String jwtToken) {
		Claims claims = parseToken(jwtToken);
		return claims != null ? claims.getSubject() : null;
	}

	public static String generateToken(User user) {
		Instant now = Instant.now();

		return Jwts.builder()
				.id(UUID.randomUUID().toString())
				.claim("role","ROLE_" + user.getRole().name())
				.issuer(ISSUER)
				.subject(user.getEmail())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(10, ChronoUnit.MINUTES)))
				.signWith(secretKey)
				.compact();
	}

}
