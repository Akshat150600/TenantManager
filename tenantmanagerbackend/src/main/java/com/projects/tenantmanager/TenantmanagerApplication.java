package com.projects.tenantmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TenantmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TenantmanagerApplication.class, args);
	}

}
