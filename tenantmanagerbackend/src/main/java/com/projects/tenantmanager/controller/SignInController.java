package com.projects.tenantmanager.controller;

import com.projects.tenantmanager.dto.AuthResponse;
import com.projects.tenantmanager.dto.LoginRequest;
import com.projects.tenantmanager.security.JwtUtil;
import com.projects.tenantmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class SignInController {
    private static final Logger logger = LoggerFactory.getLogger(SignInController.class);

    private final UserService userService;
    private final JwtUtil jwtUtil;

    SignInController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user and generates a JWT token
     * 
     * @param loginRequest The login credentials
     * @return ResponseEntity containing the JWT token if authentication is
     *         successful
     */
    @Operation(summary = "User login", description = "Authenticates a user with username and password, returns JWT token and user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticateUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Attempting authentication for user: {}", loginRequest.getUsername());

        try {
            // Authenticate user
            AuthResponse response = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

            logger.info("User authenticated successfully: {}", loginRequest.getUsername());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Authentication failed for user: {}", loginRequest.getUsername(), e);
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    /**
     * Logs out a user by invalidating their session in Redis
     * 
     * @param authorizationHeader The Authorization header containing the JWT token
     * @return ResponseEntity with success message
     */
    @Operation(summary = "User logout", description = "Invalidates user session in Redis by removing the JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid or missing token", content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("Logout attempted without valid token");
            return ResponseEntity.badRequest().body("No valid token provided");
        }

        try {
            String token = authorizationHeader.substring(7);
            String username = jwtUtil.extractUsername(token);

            logger.info("Logging out user: {}", username);
            userService.logout(username);
            logger.info("User logged out successfully: {}", username);

            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            logger.error("Logout failed", e);
            return ResponseEntity.internalServerError().body("Logout failed");
        }
    }

}
