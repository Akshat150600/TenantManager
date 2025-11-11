package com.projects.tenantmanager.repository;

import com.projects.tenantmanager.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Cacheable(value = "users", key = "#username", unless = "#result == null")
    Optional<User> findByUsername(String username);
}