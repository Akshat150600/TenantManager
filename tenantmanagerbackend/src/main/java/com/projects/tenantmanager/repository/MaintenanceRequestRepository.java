package com.projects.tenantmanager.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.projects.tenantmanager.model.MaintenanceRequest;
import com.projects.tenantmanager.model.MaintenanceRequest.Department;
import com.projects.tenantmanager.model.MaintenanceRequest.Status;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, UUID> {

    List<MaintenanceRequest> findByStatus(Status status);

    List<MaintenanceRequest> findByDepartment(Department department);

    List<MaintenanceRequest> findByStatusAndDepartment(Status status, Department department);
    
    @Query("select mr from MaintenanceRequest mr "
    		+ "where mr.unitNumber = ?1 "
    		+ "and mr.department = ?2 "
    		+ "and mr.description = ?3")
    List<MaintenanceRequest> findByUnitDepartmentDescription(String unitNumber,Department department,String description);

}
