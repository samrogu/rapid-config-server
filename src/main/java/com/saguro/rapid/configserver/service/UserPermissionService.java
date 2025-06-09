package com.saguro.rapid.configserver.service;

import com.saguro.rapid.configserver.entity.UserPermission;
import com.saguro.rapid.configserver.repository.UserPermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;

    public UserPermissionService(UserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    public List<UserPermission> getPermissionsByUsername(String username) {
        return userPermissionRepository.findByUserUsername(username);
    }

    public List<UserPermission> getPermissionsByOrganization(Long organizationId) {
        return userPermissionRepository.findByOrganizationId(organizationId);
    }

    public List<UserPermission> getPermissionsByApplication(Long applicationId) {
        return userPermissionRepository.findByApplicationId(applicationId);
    }

    public List<UserPermission> getPermissionsByUsernameAndOrganization(String username, Long organizationId) {
        return userPermissionRepository.findByUserUsernameAndOrganizationId(username, organizationId);
    }

    public List<UserPermission> getPermissionsByUsernameAndApplication(String username, Long applicationId) {
        return userPermissionRepository.findByUserUsernameAndApplicationId(username, applicationId);
    }

    public boolean hasOrganizationPermission(String username, Long organizationId) {
        return userPermissionRepository.existsByUserUsernameAndOrganizationId(username, organizationId);
    }

    public boolean hasApplicationPermission(String username, Long applicationId) {
        return userPermissionRepository.existsByUserUsernameAndApplicationId(username, applicationId);
    }

    public UserPermission savePermission(UserPermission permission) {
        return userPermissionRepository.save(permission);
    }

    public void deletePermission(Long id) {
        userPermissionRepository.deleteById(id);
    }

    public List<UserPermission> getAllPermissions() {
        return userPermissionRepository.findAll();
    }
}
