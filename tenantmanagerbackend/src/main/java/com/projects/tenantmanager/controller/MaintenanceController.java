package com.projects.tenantmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.projects.tenantmanager.model.MaintenanceRequest;
import com.projects.tenantmanager.service.MaintenanceService;
import com.projects.tenantmanager.dto.MaintenanceRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/maintenance")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Maintenance", description = "Tenant operations for maintenance requests")
@SecurityRequirement(name = "bearerAuth")
public class MaintenanceController {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceController.class);
    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @Operation(summary = "Create maintenance request", description = "Creates a new maintenance request for the authenticated tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MaintenanceRequest.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<?> createRequest(
            @RequestBody MaintenanceRequestDto requestDto,
            Authentication authentication) {
        try {
            logger.info("Received maintenance request from user: {}", authentication.getName());
            String username = authentication.getName();
            MaintenanceRequest request = maintenanceService.createRequest(requestDto, username);
            logger.info("Successfully created maintenance request with ID: {}", request.getId());
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            logger.error("Error creating maintenance request", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}