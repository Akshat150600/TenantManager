package com.projects.tenantmanager.service;

import com.projects.tenantmanager.dto.MaintenanceRequestDto;
import com.projects.tenantmanager.model.MaintenanceRequest;
import com.projects.tenantmanager.model.MaintenanceRequest.Department;
import com.projects.tenantmanager.model.MaintenanceRequest.Status;
import com.projects.tenantmanager.model.User;
import com.projects.tenantmanager.model.UserRole;
import com.projects.tenantmanager.repository.MaintenanceRequestRepository;
import com.projects.tenantmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceServiceTest {

    @Mock
    private MaintenanceRequestRepository maintenanceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MaintenanceService maintenanceService;

    private User testUser;
    private MaintenanceRequest testRequest;
    private MaintenanceRequestDto testDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("tenant1");
        testUser.setRole(UserRole.TENANT);

        testRequest = new MaintenanceRequest();
        testRequest.setId(UUID.randomUUID());
        testRequest.setTenantName("tenant1");
        testRequest.setUnitNumber("101");
        testRequest.setDescription("Leaking faucet");
        testRequest.setDepartment(Department.PLUMBING);
        testRequest.setStatus(Status.OPEN);
        testRequest.setApproved(false);

        testDto = new MaintenanceRequestDto();
        testDto.setUnitNumber("101");
        testDto.setDescription("Leaking faucet");
        testDto.setDepartment(Department.PLUMBING);
    }

    @Test
    void createRequest_Success() {
        // Arrange
        when(userRepository.findByUsername("tenant1")).thenReturn(Optional.of(testUser));
        when(maintenanceRepository.save(any(MaintenanceRequest.class))).thenReturn(testRequest);

        // Act
        MaintenanceRequest result = maintenanceService.createRequest(testDto, "tenant1");

        // Assert
        assertNotNull(result);
        assertEquals("tenant1", result.getTenantName());
        assertEquals("101", result.getUnitNumber());
        assertEquals(Department.PLUMBING, result.getDepartment());
        assertEquals(Status.OPEN, result.getStatus());
        assertFalse(result.isApproved());

        verify(userRepository).findByUsername("tenant1");
        verify(maintenanceRepository).save(any(MaintenanceRequest.class));
    }

    @Test
    void createRequest_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            maintenanceService.createRequest(testDto, "nonexistent");
        });

        verify(userRepository).findByUsername("nonexistent");
        verify(maintenanceRepository, never()).save(any());
    }

    @Test
    void getAllRequests_Success() {
        // Arrange
        List<MaintenanceRequest> requests = Arrays.asList(testRequest);
        when(maintenanceRepository.findAll()).thenReturn(requests);

        // Act
        List<MaintenanceRequest> result = maintenanceService.getAllRequests();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testRequest, result.get(0));

        verify(maintenanceRepository).findAll();
    }

    @Test
    void getRequestsByStatus_Success() {
        // Arrange
        List<MaintenanceRequest> requests = Arrays.asList(testRequest);
        when(maintenanceRepository.findByStatus(Status.OPEN)).thenReturn(requests);

        // Act
        List<MaintenanceRequest> result = maintenanceService.getRequestsByStatus("OPEN");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Status.OPEN, result.get(0).getStatus());

        verify(maintenanceRepository).findByStatus(Status.OPEN);
    }

    @Test
    void getRequestsByDepartment_Success() {
        // Arrange
        List<MaintenanceRequest> requests = Arrays.asList(testRequest);
        when(maintenanceRepository.findByDepartment(Department.PLUMBING)).thenReturn(requests);

        // Act
        List<MaintenanceRequest> result = maintenanceService.getRequestsByDepartment("PLUMBING");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Department.PLUMBING, result.get(0).getDepartment());

        verify(maintenanceRepository).findByDepartment(Department.PLUMBING);
    }

    @Test
    void getRequestsByStatusAndDepartment_Success() {
        // Arrange
        List<MaintenanceRequest> requests = Arrays.asList(testRequest);
        when(maintenanceRepository.findByStatusAndDepartment(Status.OPEN, Department.PLUMBING))
                .thenReturn(requests);

        // Act
        List<MaintenanceRequest> result = maintenanceService.getRequestsByStatusAndDepartment("OPEN", "PLUMBING");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Status.OPEN, result.get(0).getStatus());
        assertEquals(Department.PLUMBING, result.get(0).getDepartment());

        verify(maintenanceRepository).findByStatusAndDepartment(Status.OPEN, Department.PLUMBING);
    }

    @Test
    void approveRequest_Success() {
        // Arrange
        UUID requestId = testRequest.getId();
        when(maintenanceRepository.findById(requestId)).thenReturn(Optional.of(testRequest));
        when(maintenanceRepository.save(any(MaintenanceRequest.class))).thenReturn(testRequest);

        // Act
        MaintenanceRequest result = maintenanceService.approveRequest(requestId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isApproved());
        assertEquals(Status.IN_PROGRESS, result.getStatus());

        verify(maintenanceRepository).findById(requestId);
        verify(maintenanceRepository).save(testRequest);
    }

    @Test
    void rejectRequest_Success() {
        // Arrange
        UUID requestId = testRequest.getId();
        when(maintenanceRepository.findById(requestId)).thenReturn(Optional.of(testRequest));
        when(maintenanceRepository.save(any(MaintenanceRequest.class))).thenReturn(testRequest);

        // Act
        MaintenanceRequest result = maintenanceService.rejectRequest(requestId);

        // Assert
        assertNotNull(result);
        assertFalse(result.isApproved());
        assertEquals(Status.REJECTED, result.getStatus());

        verify(maintenanceRepository).findById(requestId);
        verify(maintenanceRepository).save(testRequest);
    }

    @Test
    void updateStatus_Success() {
        // Arrange
        UUID requestId = testRequest.getId();
        when(maintenanceRepository.findById(requestId)).thenReturn(Optional.of(testRequest));
        when(maintenanceRepository.save(any(MaintenanceRequest.class))).thenReturn(testRequest);

        // Act
        MaintenanceRequest result = maintenanceService.updateStatus(requestId, "RESOLVED");

        // Assert
        assertNotNull(result);
        assertEquals(Status.RESOLVED, result.getStatus());

        verify(maintenanceRepository).findById(requestId);
        verify(maintenanceRepository).save(testRequest);
    }

    @Test
    void approveRequest_NotFound_ThrowsException() {
        // Arrange
        UUID requestId = UUID.randomUUID();
        when(maintenanceRepository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            maintenanceService.approveRequest(requestId);
        });

        verify(maintenanceRepository).findById(requestId);
        verify(maintenanceRepository, never()).save(any());
    }

    @Test
    void rejectRequest_NotFound_ThrowsException() {
        // Arrange
        UUID requestId = UUID.randomUUID();
        when(maintenanceRepository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            maintenanceService.rejectRequest(requestId);
        });

        verify(maintenanceRepository).findById(requestId);
        verify(maintenanceRepository, never()).save(any());
    }

    @Test
    void updateStatus_NotFound_ThrowsException() {
        // Arrange
        UUID requestId = UUID.randomUUID();
        when(maintenanceRepository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            maintenanceService.updateStatus(requestId, "RESOLVED");
        });

        verify(maintenanceRepository).findById(requestId);
        verify(maintenanceRepository, never()).save(any());
    }
}
