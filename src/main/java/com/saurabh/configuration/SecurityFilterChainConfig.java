package com.saurabh.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableMethodSecurity
@Configuration
public class SecurityFilterChainConfig {

    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final JWTAuthenticationFilter jwtAuthenticationfilter;
    private final RateLimitingFilter rateLimitingFilter;

    @Value("${app.cors.allowed-origins:http://localhost:5173,https://job-portal-frontend-mocha-ten.vercel.app,https://*.vercel.app}")
    private String allowedOrigins;

    public SecurityFilterChainConfig(AuthenticationEntryPoint authenticationEntryPoint,
            JWTAuthenticationFilter jwtAuthenticationFilter,
            RateLimitingFilter rateLimitingFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAuthenticationfilter = jwtAuthenticationFilter;
        this.rateLimitingFilter = rateLimitingFilter;
    }
    
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allowed origins (Frontend URLs)
        List<String> originList = java.util.Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        configuration.setAllowedOriginPatterns(originList);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Set-Cookie", "Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {  	
    	httpSecurity
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        httpSecurity.authorizeHttpRequests(auth -> auth
                // Actuator health check for cloud deployments (Render, Railway, Kubernetes)
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // Public endpoints
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/v1/skills").permitAll()
                // Public job browsing (GET only)
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/jobs", "/api/v1/jobs/**").permitAll()
                // Role-scoped routes
                .requestMatchers("/api/v1/employer/**").hasRole("EMPLOYER")
                .requestMatchers("/api/v1/jobseekers/**").hasRole("JOBSEEKER")
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        );

        // Mitigate clickjacking: allow framing only from the same origin
        httpSecurity.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        httpSecurity.exceptionHandling(ex ->
                ex.authenticationEntryPoint(authenticationEntryPoint));

        httpSecurity.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.addFilterBefore(jwtAuthenticationfilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
