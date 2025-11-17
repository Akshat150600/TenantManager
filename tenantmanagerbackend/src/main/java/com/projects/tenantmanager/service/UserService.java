package com.projects.tenantmanager.service;

import com.projects.tenantmanager.model.User;
import com.projects.tenantmanager.repository.UserRepository;
import com.projects.tenantmanager.security.JwtUtil;
import com.projects.tenantmanager.dto.AuthResponse;
import com.projects.tenantmanager.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisSessionService redisSessionService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
            RedisSessionService redisSessionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.redisSessionService = redisSessionService;
    }

    /**
     * Authenticates a user and returns AuthResponse with JWT token
     * 
     * @param username The username to authenticate
     * @param password The raw password to verify
     * @return AuthResponse containing JWT token and user details
     * @throws BadCredentialsException if authentication fails
     */
    public AuthResponse authenticate(String username, String password) {
        logger.debug("Attempting to authenticate user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new BadCredentialsException("Invalid credentials");
                });
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.error("Invalid password for user: {}", username);
            throw new BadCredentialsException("Invalid credentials");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        logger.info("User authenticated successfully: {}", username);

        // Return AuthResponse with token and user details
        return new AuthResponse(
                token,
                new UserDto(user.getUsername(), user.getRole().name()));
    }

    /**
     * Logs out a user by invalidating their session in Redis
     * 
     * @param username The username to log out
     */
    public void logout(String username) {
        logger.debug("Logging out user: {}", username);
        redisSessionService.invalidateSession(username);
        logger.info("User logged out successfully: {}", username);
    }
}