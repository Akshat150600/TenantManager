package com.projects.tenantmanager.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.tenantmanager.dto.MaintenanceRequestDto;
import com.projects.tenantmanager.model.MaintenanceRequest;
import com.projects.tenantmanager.model.MaintenanceRequest.Status;
import com.projects.tenantmanager.model.User;
import com.projects.tenantmanager.repository.MaintenanceRequestRepository;
import com.projects.tenantmanager.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class MaintenanceService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceService.class);

    private final MaintenanceRequestRepository maintenanceRepository;
    private final UserRepository userRepository;

    public MaintenanceService(MaintenanceRequestRepository maintenanceRepository,
            UserRepository userRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.userRepository = userRepository;
    }

    @CacheEvict(value = "maintenanceRequests", allEntries = true)
    public MaintenanceRequest createRequest(MaintenanceRequestDto requestDto, String username) {
        logger.debug("Creating maintenance request for user: {}", username);

        // Find the tenant by username
        User tenant = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        // Create new maintenance request
        MaintenanceRequest request = new MaintenanceRequest();
        request.setTenantName(tenant.getUsername());
        request.setUnitNumber(requestDto.getUnitNumber());
        request.setDescription(requestDto.getDescription());
        request.setDepartment(requestDto.getDepartment());
        request.setStatus(Status.OPEN);
        request.setApproved(false);

        logger.info("Saving maintenance request for tenant: {}, unit: {}, department: {}",
                tenant.getUsername(), requestDto.getUnitNumber(), requestDto.getDepartment());

        // Save and return the request
        MaintenanceRequest savedRequest = maintenanceRepository.save(request);

        logger.debug("Maintenance request created successfully with ID: {}", savedRequest.getId());

        return savedRequest;
    }

    @Cacheable(value = "maintenanceRequests", key = "'all'")
    public List<MaintenanceRequest> getAllRequests() {
        logger.debug("Fetching all maintenance requests");
        return maintenanceRepository.findAll();
    }

    @Cacheable(value = "maintenanceRequests", key = "#status")
    public List<MaintenanceRequest> getRequestsByStatus(String status) {
        logger.debug("Fetching maintenance requests with status: {}", status);
        Status statusEnum = Status.valueOf(status);
        return maintenanceRepository.findByStatus(statusEnum);
    }

    @CacheEvict(value = "maintenanceRequests", allEntries = true)
    public MaintenanceRequest approveRequest(UUID id) {
        logger.info("Approving maintenance request: {}", id);
        MaintenanceRequest request = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setApproved(true);
        request.setStatus(Status.IN_PROGRESS);

        logger.debug("Request approved and status updated to IN_PROGRESS");
        return maintenanceRepository.save(request);
    }

    @CacheEvict(value = "maintenanceRequests", allEntries = true)
    public MaintenanceRequest rejectRequest(UUID id) {
        logger.info("Rejecting maintenance request: {}", id);
        MaintenanceRequest request = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setApproved(false);
        request.setStatus(Status.REJECTED);

        logger.debug("Request rejected and status updated to REJECTED");
        return maintenanceRepository.save(request);
    }

    @CacheEvict(value = "maintenanceRequests", allEntries = true)
    public MaintenanceRequest updateStatus(UUID id, String status) {
        logger.info("Updating status for request: {} to {}", id, status);
        MaintenanceRequest request = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Status statusEnum = Status.valueOf(status);
        request.setStatus(statusEnum);

        logger.debug("Status updated successfully");
        return maintenanceRepository.save(request);
    }

    @Cacheable(value = "maintenanceRequests", key = "#department")
    public List<MaintenanceRequest> getRequestsByDepartment(String department) {
        logger.debug("Fetching maintenance requests with department: {}", department);
        MaintenanceRequest.Department deptEnum = MaintenanceRequest.Department.valueOf(department);
        return maintenanceRepository.findByDepartment(deptEnum);
    }

    @Cacheable(value = "maintenanceRequests", key = "#status + '_' + #department")
    public List<MaintenanceRequest> getRequestsByStatusAndDepartment(String status, String department) {
        logger.debug("Fetching maintenance requests with status: {} and department: {}", status, department);
        Status statusEnum = Status.valueOf(status);
        MaintenanceRequest.Department deptEnum = MaintenanceRequest.Department.valueOf(department);
        return maintenanceRepository.findByStatusAndDepartment(statusEnum, deptEnum);
    }

	public boolean checkRequestExist(MaintenanceRequestDto requestDto) {
		
		boolean isDuplicated = false;
		List<MaintenanceRequest> requestFromDb = maintenanceRepository.findByUnitDepartmentDescription(requestDto.getUnitNumber(),
				requestDto.getDepartment(),
				requestDto.getDescription());
		
		if(!requestFromDb.isEmpty()) isDuplicated = true;
		
		return isDuplicated;
	}

}
