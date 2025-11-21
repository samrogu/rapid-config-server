package com.saguro.rapid.configserver.service;

import com.saguro.rapid.configserver.dto.ApplicationDTO;
import com.saguro.rapid.configserver.entity.Application;
import com.saguro.rapid.configserver.entity.Organization;
import com.saguro.rapid.configserver.entity.UserPermission;
import com.saguro.rapid.configserver.mapper.ApplicationMapper;
import com.saguro.rapid.configserver.repository.ApplicationRepository;
import com.saguro.rapid.configserver.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;
    private final OrganizationRepository organizationRepository;

    public ApplicationService(ApplicationRepository applicationRepository, ApplicationMapper applicationMapper,
            OrganizationRepository organizationRepository) {
        this.applicationRepository = applicationRepository;
        this.applicationMapper = applicationMapper;
        this.organizationRepository = organizationRepository;
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ApplicationDTO getApplicationById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        return applicationMapper.toDTO(application);
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsByOrganization(Long organizationId) {
        return applicationRepository.findByOrganizationId(organizationId).stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ApplicationDTO createApplication(Long organizationId, ApplicationDTO applicationDTO) {
        // Buscar la organización por ID
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        // Convertir el DTO a la entidad
        Application application = applicationMapper.toEntity(applicationDTO);

        // Asignar la organización al objeto Application
        application.setOrganization(organization);

        // Guardar la aplicación en la base de datos
        Application savedApplication = applicationRepository.save(application);

        // Convertir la entidad guardada a DTO y devolverla
        return applicationMapper.toDTO(savedApplication);
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getEnabledApplicationsByOrganization(Long organizationId) {
        return applicationRepository.findByEnabledTrueAndOrganizationId(organizationId).stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ApplicationDTO> getEnabledApplicationsByOrganizationAndName(Long organizationId, String name) {
        return applicationRepository.findByEnabledTrueAndOrganizationIdAndName(organizationId, name).stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ApplicationDTO updateApplication(Long id, ApplicationDTO applicationDTO) {
        // Buscar la aplicación existente
        Application existingApplication = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        // Actualizar los campos de la aplicación existente usando MapStruct
        applicationMapper.updateApplicationFromDto(applicationDTO, existingApplication);

        // Guardar los cambios en la base de datos
        Application updatedApplication = applicationRepository.save(existingApplication);

        // Convertir la entidad actualizada a DTO y devolverla
        return applicationMapper.toDTO(updatedApplication);
    }

    @Transactional(readOnly = true)
    public Optional<Application> findByOrganizationAndUidAndMicroservice(
            String uidOrg, String uidApp, String label) {
        return applicationRepository.findByOrganizationUidAndUidAndLabel(
                uidOrg, uidApp, label);
    }

    /**
     * Get all applications accessible by the user.
     * If user is Admin, returns all applications.
     * Otherwise, returns only applications the user has read permission for, with
     * sensitive fields masked.
     */
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsForUser(String username, UserPermissionService userPermissionService) {
        if (userPermissionService.isAdmin(username)) {
            return getAllApplications();
        }

        List<UserPermission> permissions = userPermissionService.getPermissionsByUsername(username);
        List<Long> applicationIds = permissions.stream()
                .filter(UserPermission::isCanRead)
                .map(p -> p.getApplication() != null ? p.getApplication().getId() : null)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        return applicationRepository.findAllById(applicationIds).stream()
                .map(applicationMapper::toDTO)
                .map(this::maskSensitiveFields)
                .collect(Collectors.toList());
    }

    /**
     * Get applications by organization accessible by the user.
     * If user is Admin, returns all applications for the organization.
     * Otherwise, returns only applications the user has read permission for in that
     * organization, with sensitive fields masked.
     */
    @Transactional(readOnly = true)
    public List<ApplicationDTO> getApplicationsByOrganizationForUser(Long organizationId, String username,
            UserPermissionService userPermissionService) {
        if (userPermissionService.isAdmin(username)) {
            return getApplicationsByOrganization(organizationId);
        }

        List<UserPermission> permissions = userPermissionService
                .getPermissionsByUsernameAndOrganization(username, organizationId);

        List<Long> applicationIds = permissions.stream()
                .filter(UserPermission::isCanRead)
                .map(p -> p.getApplication() != null ? p.getApplication().getId() : null)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        return applicationRepository.findAllById(applicationIds).stream()
                .filter(app -> app.getOrganization().getId().equals(organizationId))
                .map(applicationMapper::toDTO)
                .map(this::maskSensitiveFields)
                .collect(Collectors.toList());
    }

    /**
     * Get application by ID for a specific user.
     * If user is Admin, returns the application with all fields.
     * If user is not Admin, checks read permission and returns application with
     * sensitive fields masked.
     */
    @Transactional(readOnly = true)
    public ApplicationDTO getApplicationByIdForUser(Long id, String username,
            UserPermissionService userPermissionService) {
        ApplicationDTO appDTO = getApplicationById(id);

        if (userPermissionService.isAdmin(username)) {
            return appDTO;
        }

        // Check permission (although controller should have checked, double check here
        // doesn't hurt)
        if (!userPermissionService.canReadApplication(username, id)) {
            throw new org.springframework.security.access.AccessDeniedException("Access is denied");
        }

        return maskSensitiveFields(appDTO);
    }

    private ApplicationDTO maskSensitiveFields(ApplicationDTO dto) {
        dto.setVaultToken(null);
        dto.setAppRoleSecret(null);
        dto.setVaultUsername(null);
        dto.setVaultPassword(null);
        return dto;
    }
}
