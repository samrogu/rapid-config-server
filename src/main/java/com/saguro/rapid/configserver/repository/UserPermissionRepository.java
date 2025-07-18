package com.saguro.rapid.configserver.repository;

import com.saguro.rapid.configserver.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    // Buscar permisos por usuario
    List<UserPermission> findByUserUsername(String username);

    // Buscar permisos por organización
    List<UserPermission> findByOrganizationId(Long organizationId);

    // Buscar permisos por aplicación
    List<UserPermission> findByApplicationId(Long applicationId);

    // Buscar permisos por usuario y organización
    List<UserPermission> findByUserUsernameAndOrganizationId(String username, Long organizationId);

    // Buscar permisos por usuario y aplicación
    List<UserPermission> findByUserUsernameAndApplicationId(String username, Long applicationId);

    boolean existsByUserUsernameAndOrganizationId(String username, Long organizationId);

    boolean existsByUserUsernameAndApplicationId(String username, Long applicationId);
}
