package com.saguro.rapid.configserver.service;

import com.saguro.rapid.configserver.entity.User;
import com.saguro.rapid.configserver.entity.Organization;
import com.saguro.rapid.configserver.entity.Application;
import com.saguro.rapid.configserver.entity.UserPermission;
import com.saguro.rapid.configserver.repository.UserRepository;
import com.saguro.rapid.configserver.repository.OrganizationRepository;
import com.saguro.rapid.configserver.repository.ApplicationRepository;
import com.saguro.rapid.configserver.repository.UserPermissionRepository;
import com.saguro.rapid.configserver.dto.UserPermissionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final ApplicationRepository applicationRepository;

    public UserPermissionService(UserPermissionRepository userPermissionRepository,
                                 UserRepository userRepository,
                                 OrganizationRepository organizationRepository,
                                 ApplicationRepository applicationRepository) {
        this.userPermissionRepository = userPermissionRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.applicationRepository = applicationRepository;
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

    public UserPermission createPermission(UserPermissionDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Organization organization = organizationRepository.findById(dto.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        Application application = null;
        if (dto.getApplicationId() != null) {
            application = applicationRepository.findById(dto.getApplicationId())
                    .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        }

        UserPermission permission = new UserPermission();
        permission.setUser(user);
        permission.setOrganization(organization);
        permission.setApplication(application);
        permission.setCanRead(dto.isCanRead());
        permission.setCanCreate(dto.isCanCreate());
        permission.setCanUpdate(dto.isCanUpdate());
        permission.setCanDelete(dto.isCanDelete());

        return userPermissionRepository.save(permission);
    }

    public void deletePermission(Long id) {
        userPermissionRepository.deleteById(id);
    }

    public List<UserPermission> getAllPermissions() {
        return userPermissionRepository.findAll();
    }
}
