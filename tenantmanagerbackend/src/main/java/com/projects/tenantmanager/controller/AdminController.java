package com.projects.tenantmanager.controller;

import com.projects.tenantmanager.model.MaintenanceRequest;
import com.projects.tenantmanager.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Admin", description = "Admin operations for managing maintenance requests")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final MaintenanceService maintenanceService;

    public AdminController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @Operation(summary = "Get all maintenance requests", description = "Retrieves all maintenance requests with optional filtering by status and/or department")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requests retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MaintenanceRequest.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)
    })
    @GetMapping("/maintenance")
    public ResponseEntity<List<MaintenanceRequest>> getAllRequests(
            @Parameter(description = "Filter by status (PENDING, IN_PROGRESS, COMPLETED, REJECTED, or ALL)") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by department (PLUMBING, ELECTRICAL, HVAC, etc., or ALL)") @RequestParam(required = false) String department) {
        logger.info("Fetching maintenance requests with status filter: {} and department filter: {}", status,
                department);
        try {
            List<MaintenanceRequest> requests;

            // Filter by both status and department if provided
            if (status != null && !status.equals("ALL") && department != null && !department.equals("ALL")) {
                requests = maintenanceService.getRequestsByStatusAndDepartment(status, department);
            } else if (status != null && !status.equals("ALL")) {
                requests = maintenanceService.getRequestsByStatus(status);
            } else if (department != null && !department.equals("ALL")) {
                requests = maintenanceService.getRequestsByDepartment(department);
            } else {
                requests = maintenanceService.getAllRequests();
            }

            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            logger.error("Error fetching maintenance requests", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Approve maintenance request", description = "Changes the status of a maintenance request to COMPLETED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request approved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MaintenanceRequest.class))),
            @ApiResponse(responseCode = "400", description = "Error approving request", content = @Content)
    })
    @PutMapping("/maintenance/{id}/approve")
    public ResponseEntity<?> approveRequest(
            @Parameter(description = "ID of the maintenance request to approve") @PathVariable UUID id) {
        logger.info("Approving maintenance request: {}", id);
        try {
            MaintenanceRequest request = maintenanceService.approveRequest(id);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            logger.error("Error approving request", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Reject maintenance request", description = "Changes the status of a maintenance request to REJECTED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request rejected successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MaintenanceRequest.class))),
            @ApiResponse(responseCode = "400", description = "Error rejecting request", content = @Content)
    })
    @PutMapping("/maintenance/{id}/reject")
    public ResponseEntity<?> rejectRequest(
            @Parameter(description = "ID of the maintenance request to reject") @PathVariable UUID id) {
        logger.info("Rejecting maintenance request: {}", id);
        try {
            MaintenanceRequest request = maintenanceService.rejectRequest(id);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            logger.error("Error rejecting request", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update maintenance request status", description = "Updates the status of a maintenance request to a custom value")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MaintenanceRequest.class))),
            @ApiResponse(responseCode = "400", description = "Error updating status", content = @Content)
    })
    @PutMapping("/maintenance/{id}/status")
    public ResponseEntity<?> updateStatus(
            @Parameter(description = "ID of the maintenance request") @PathVariable UUID id,
            @RequestBody Map<String, String> statusUpdate) {
        logger.info("Updating status for request: {} to {}", id, statusUpdate.get("status"));
        try {
            String status = statusUpdate.get("status");
            MaintenanceRequest request = maintenanceService.updateStatus(id, status);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            logger.error("Error updating status", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}