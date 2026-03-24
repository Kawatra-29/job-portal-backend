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
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable());

        httpSecurity.authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/v1/skills").permitAll()
                // Public job browsing (GET only — write operations protected by @PreAuthorize)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/jobs", "/api/v1/jobs/**").permitAll()
                // Role-scoped routes
                .requestMatchers("/api/v1/employer/**").hasRole("EMPLOYER")
                .requestMatchers("/api/v1/jobseekers/**").hasRole("JOBSEEKER")
                // Fixed: was "/api/v1/admin/**" but AdminController was mapped to "/admin".
                // Both are now aligned to "/api/v1/admin/**".
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        httpSecurity.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        httpSecurity.exceptionHandling(ex ->
                ex.authenticationEntryPoint(authenticationEntryPoint));

        httpSecurity.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.addFilterBefore(jwtAuthenticationfilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}