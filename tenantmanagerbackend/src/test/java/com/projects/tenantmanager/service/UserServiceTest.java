package com.projects.tenantmanager.service;

import com.projects.tenantmanager.dto.AuthResponse;
import com.projects.tenantmanager.model.User;
import com.projects.tenantmanager.model.UserRole;
import com.projects.tenantmanager.repository.UserRepository;
import com.projects.tenantmanager.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisSessionService redisSessionService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.TENANT);
    }

    @Test
    void authenticate_Success() {
        // Arrange
        String rawPassword = "password123";
        String token = "jwt-token-123";

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(rawPassword, testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn(token);
        doNothing().when(redisSessionService).storeUserSession(anyString(), anyString());

        // Act
        AuthResponse response = userService.authenticate("testuser", rawPassword);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.getToken());
        assertEquals("testuser", response.getUser().getUsername());
        assertEquals("TENANT", response.getUser().getRole());

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches(rawPassword, testUser.getPassword());
        verify(jwtUtil).generateToken("testuser", "TENANT");
        verify(redisSessionService).storeUserSession("testuser", token);
    }

    @Test
    void authenticate_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            userService.authenticate("nonexistent", "password");
        });

        verify(userRepository).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void authenticate_InvalidPassword_ThrowsException() {
        // Arrange
        String wrongPassword = "wrongpassword";
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(wrongPassword, testUser.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            userService.authenticate("testuser", wrongPassword);
        });

        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches(wrongPassword, testUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void logout_Success() {
        // Arrange
        doNothing().when(redisSessionService).invalidateSession("testuser");

        // Act
        userService.logout("testuser");

        // Assert
        verify(redisSessionService).invalidateSession("testuser");
    }
}
