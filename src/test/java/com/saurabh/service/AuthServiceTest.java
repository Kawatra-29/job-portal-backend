package com.saurabh.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.saurabh.DTOs.AuthRequestDto;
import com.saurabh.DTOs.AuthResponseDto;
import com.saurabh.DTOs.LoginDto;
import com.saurabh.ENUMS.AuthStatus;
import com.saurabh.ENUMS.Role;
import com.saurabh.Entity.User;
import com.saurabh.repository.EmployerRepository;
import com.saurabh.repository.JobseekerRepository;
import com.saurabh.repository.OtpRepository;
import com.saurabh.repository.PasswordResetRepository;
import com.saurabh.repository.UserRepository;
import com.saurabh.util.JwtUtils;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JobseekerRepository jobseekerRepository;

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @SuppressWarnings("null")
	@BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "otpRepository", otpRepository);
        ReflectionTestUtils.setField(authService, "emailService", emailService);
        ReflectionTestUtils.setField(authService, "passwordResetRepository", passwordResetRepository);
    }

    @Test
    @DisplayName("Login() - Successfully returns JWT Token when email is verified")
    void testLogin_Success() {
        // Arrange
        LoginDto loginDto = new LoginDto("saurabh@gmail.com", "password123");
        User user = new User();
        user.setEmail("saurabh@gmail.com");
        user.setIsEmailVerified(true);
        user.setRole(Role.JOBSEEKER);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtils.generateToken(user)).thenReturn("mocked-jwt-token");

        // Act
        AuthResponseDto response = authService.Login(loginDto);

        // Assert
        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.token());
        assertEquals(AuthStatus.LOGIN_SUCCESS, response.authStatus());
    }

    @SuppressWarnings("null")
	@Test
    @DisplayName("signup() - Returns USER_ALREADY_EXISTS if email already in DB")
    void testSignup_UserAlreadyExists() {
        // Arrange
        AuthRequestDto request = mock(AuthRequestDto.class);
        when(request.email()).thenReturn("test@gmail.com");
        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        // Act
        AuthResponseDto response = authService.signup(request);

        // Assert
        assertEquals(AuthStatus.USER_ALREADY_EXISTS, response.authStatus());
        verify(userRepository, never()).save(any(User.class));
    }

    @SuppressWarnings("null")
	@Test
    @DisplayName("signup() - Creates user and sends OTP email on valid registration")
    void testSignup_Success() {
        // Arrange
        AuthRequestDto request = mock(AuthRequestDto.class);
        when(request.email()).thenReturn("new@gmail.com");
        when(request.password()).thenReturn("pass123");
        when(request.role()).thenReturn(Role.JOBSEEKER);
        when(request.fname()).thenReturn("Saurabh");
        when(request.phone()).thenReturn("9999999999");

        when(userRepository.existsByEmail("new@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encodedPassword");
        when(otpRepository.findByEmail("new@gmail.com")).thenReturn(Optional.empty());

        // Act
        AuthResponseDto response = authService.signup(request);

        // Assert
        assertEquals(AuthStatus.USER_CREATED_SUCCESSFULLY, response.authStatus());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendOtpEmail(eq("new@gmail.com"), anyString());
    }
}
