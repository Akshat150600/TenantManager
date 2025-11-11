package com.projects.tenantmanager.dto;

import com.projects.tenantmanager.model.MaintenanceRequest.Department;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
public class MaintenanceRequestDto {
    private String unitNumber;
    private String description;
    private Department department;

    public MaintenanceRequestDto() {
    }

    public MaintenanceRequestDto(String unitNumber, String description, Department department) {
        this.unitNumber = unitNumber;
        this.description = description;
        this.department = department;
    }

    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}