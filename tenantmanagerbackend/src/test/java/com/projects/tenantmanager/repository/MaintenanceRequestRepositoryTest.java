package com.projects.tenantmanager.repository;

import com.projects.tenantmanager.model.MaintenanceRequest;
import com.projects.tenantmanager.model.MaintenanceRequest.Department;
import com.projects.tenantmanager.model.MaintenanceRequest.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class MaintenanceRequestRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    @Test
    void findByStatus_ReturnsCorrectRequests() {
        // Arrange
        MaintenanceRequest request1 = new MaintenanceRequest();
        request1.setTenantName("tenant1");
        request1.setUnitNumber("101");
        request1.setDescription("Fix leak");
        request1.setDepartment(Department.PLUMBING);
        request1.setStatus(Status.OPEN);
        entityManager.persist(request1);

        MaintenanceRequest request2 = new MaintenanceRequest();
        request2.setTenantName("tenant2");
        request2.setUnitNumber("102");
        request2.setDescription("Fix lights");
        request2.setDepartment(Department.ELECTRICAL);
        request2.setStatus(Status.RESOLVED);
        entityManager.persist(request2);

        entityManager.flush();

        // Act
        List<MaintenanceRequest> openRequests = maintenanceRequestRepository.findByStatus(Status.OPEN);

        // Assert
        assertEquals(1, openRequests.size());
        assertEquals("tenant1", openRequests.get(0).getTenantName());
        assertEquals(Status.OPEN, openRequests.get(0).getStatus());
    }

    @Test
    void findByDepartment_ReturnsCorrectRequests() {
        // Arrange
        MaintenanceRequest request1 = new MaintenanceRequest();
        request1.setTenantName("tenant1");
        request1.setUnitNumber("101");
        request1.setDescription("Fix leak");
        request1.setDepartment(Department.PLUMBING);
        request1.setStatus(Status.OPEN);
        entityManager.persist(request1);

        MaintenanceRequest request2 = new MaintenanceRequest();
        request2.setTenantName("tenant2");
        request2.setUnitNumber("102");
        request2.setDescription("Fix AC");
        request2.setDepartment(Department.HVAC);
        request2.setStatus(Status.OPEN);
        entityManager.persist(request2);

        entityManager.flush();

        // Act
        List<MaintenanceRequest> plumbingRequests = maintenanceRequestRepository.findByDepartment(Department.PLUMBING);

        // Assert
        assertEquals(1, plumbingRequests.size());
        assertEquals("tenant1", plumbingRequests.get(0).getTenantName());
        assertEquals(Department.PLUMBING, plumbingRequests.get(0).getDepartment());
    }

    @Test
    void findByStatusAndDepartment_ReturnsCorrectRequests() {
        // Arrange
        MaintenanceRequest request1 = new MaintenanceRequest();
        request1.setTenantName("tenant1");
        request1.setUnitNumber("101");
        request1.setDescription("Fix leak");
        request1.setDepartment(Department.PLUMBING);
        request1.setStatus(Status.OPEN);
        entityManager.persist(request1);

        MaintenanceRequest request2 = new MaintenanceRequest();
        request2.setTenantName("tenant2");
        request2.setUnitNumber("102");
        request2.setDescription("Fix broken pipe");
        request2.setDepartment(Department.PLUMBING);
        request2.setStatus(Status.RESOLVED);
        entityManager.persist(request2);

        MaintenanceRequest request3 = new MaintenanceRequest();
        request3.setTenantName("tenant3");
        request3.setUnitNumber("103");
        request3.setDescription("Fix lights");
        request3.setDepartment(Department.ELECTRICAL);
        request3.setStatus(Status.OPEN);
        entityManager.persist(request3);

        entityManager.flush();

        // Act
        List<MaintenanceRequest> openPlumbingRequests = maintenanceRequestRepository
                .findByStatusAndDepartment(Status.OPEN, Department.PLUMBING);

        // Assert
        assertEquals(1, openPlumbingRequests.size());
        assertEquals("tenant1", openPlumbingRequests.get(0).getTenantName());
        assertEquals(Status.OPEN, openPlumbingRequests.get(0).getStatus());
        assertEquals(Department.PLUMBING, openPlumbingRequests.get(0).getDepartment());
    }

    @Test
    void saveMaintenanceRequest_Success() {
        // Arrange
        MaintenanceRequest request = new MaintenanceRequest();
        request.setTenantName("tenant1");
        request.setUnitNumber("101");
        request.setDescription("Fix door");
        request.setDepartment(Department.CARPENTRY);
        request.setStatus(Status.OPEN);
        request.setApproved(false);

        // Act
        MaintenanceRequest saved = maintenanceRequestRepository.save(request);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("tenant1", saved.getTenantName());
        assertEquals(Department.CARPENTRY, saved.getDepartment());
        assertFalse(saved.isApproved());
    }
}
