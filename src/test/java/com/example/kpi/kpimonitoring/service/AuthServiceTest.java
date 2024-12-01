package com.example.kpi.kpimonitoring.service;

import com.example.kpi.dto.UserRequest;
import com.example.kpi.entities.User;
import com.example.kpi.exceptions.ResourceExistException;
import com.example.kpi.exceptions.ResourceNotFoundException;
import com.example.kpi.repositories.UserRepository;
import com.example.kpi.security.JwtTokenProvider;
import com.example.kpi.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@SpringBootTest

public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private UserRequest userRequest;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        userRequest = new UserRequest("test@example.com", "password123", null);
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword123");
        user.setRoles(null);
    }

    @Test
    public void register_whenEmailExists_shouldThrowResourceExistException() {
        when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);

        assertThrows(ResourceExistException.class, () -> authService.register(userRequest));
    }

    @Test
    public void register_whenEmailNotExists_shouldSaveUser() throws ResourceExistException {
        when(userRepository.existsByEmail(userRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.password())).thenReturn("encodedPassword123");

        authService.register(userRequest);

        Mockito.verify(userRepository).save(any(User.class));
    }

    @Test
    public void authenticate_whenUserNotFound_shouldThrowResourceNotFoundException() {
        when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.authenticate(userRequest.email(), userRequest.password()));
    }

    @Test
    public void authenticate_whenPasswordIncorrect_shouldThrowResourceNotFoundException() {
        when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userRequest.password(), user.getPassword())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> authService.authenticate(userRequest.email(), userRequest.password()));
    }

    @Test
    public void authenticate_whenCredentialsAreCorrect_shouldReturnJwtToken() {
        when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(userRequest.password(), user.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(user)).thenReturn("mockJwtToken");

        String token = authService.authenticate(userRequest.email(), userRequest.password());

        assertNotNull(token);
        assertEquals("mockJwtToken", token);
    }
}
