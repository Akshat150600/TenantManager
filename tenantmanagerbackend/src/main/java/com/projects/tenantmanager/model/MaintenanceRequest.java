package com.projects.tenantmanager.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "maintenance_request")
@Getter
@Setter
public class MaintenanceRequest {
	@Id
	@GeneratedValue
	private UUID id;
	private String tenantName;
	private String unitNumber;

	@Column(length = 2000)
	private String description;

	@Enumerated(EnumType.STRING)
	private Status status = Status.OPEN;

	@Enumerated(EnumType.STRING)
	private Department department;

	private boolean approved = false;

	@CreationTimestamp
	private Instant createdAt;

	@UpdateTimestamp
	private Instant updatedAt;

	public enum Status {
		OPEN, IN_PROGRESS, RESOLVED, REJECTED
	}

	public enum Department {
		PLUMBING,
		ELECTRICAL,
		HVAC,
		CARPENTRY,
		HOUSEHOLD_FIX,
		PAINTING,
		CLEANING,
		PEST_CONTROL,
		APPLIANCE_REPAIR,
		GENERAL_MAINTENANCE
	}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
