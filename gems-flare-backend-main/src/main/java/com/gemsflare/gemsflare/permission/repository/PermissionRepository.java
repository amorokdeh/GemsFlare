package com.gemsflare.gemsflare.permission.repository;

import com.gemsflare.gemsflare.permission.jpa.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {
    Optional<PermissionEntity> findByRoute(String route);
}