package com.projects.tenantmanager.dto;

import com.projects.tenantmanager.model.MaintenanceRequest.Department;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceRequestDto {
    private String unitNumber;
    private String description;
    private Department department;

}