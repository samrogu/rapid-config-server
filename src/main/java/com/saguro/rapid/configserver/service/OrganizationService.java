package com.saguro.rapid.configserver.service;

import com.saguro.rapid.configserver.dto.CountDTO;
import com.saguro.rapid.configserver.dto.OrganizationDTO;
import com.saguro.rapid.configserver.entity.Organization;
import com.saguro.rapid.configserver.entity.UserPermission;
import com.saguro.rapid.configserver.mapper.OrganizationMapper;
import com.saguro.rapid.configserver.repository.ApplicationRepository;
import com.saguro.rapid.configserver.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final ApplicationRepository applicationRepository;

    public OrganizationService(OrganizationRepository organizationRepository, OrganizationMapper organizationMapper,
            ApplicationRepository applicationRepository) {
        this.organizationRepository = organizationRepository;
        this.organizationMapper = organizationMapper;
        this.applicationRepository = applicationRepository;
    }

    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public OrganizationDTO getOrganizationById(Long id) {
        Organization organization = organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        return organizationMapper.toDTO(organization);
    }

    public OrganizationDTO createOrganization(OrganizationDTO organizationDTO) {
        Organization organization = organizationMapper.toEntity(organizationDTO);
        Organization savedOrganization = organizationRepository.save(organization);
        return organizationMapper.toDTO(savedOrganization);
    }

    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }

    public OrganizationDTO updateOrganization(Long id, OrganizationDTO organizationDTO) {
        // Buscar la organización existente
        Organization existingOrganization = organizationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));

        // Actualizar los campos de la organización existente
        existingOrganization.setName(organizationDTO.getName());
        existingOrganization.setDescription(organizationDTO.getDescription());

        // Guardar los cambios en la base de datos
        Organization updatedOrganization = organizationRepository.save(existingOrganization);

        // Convertir la entidad actualizada a DTO y devolverla
        return organizationMapper.toDTO(updatedOrganization);
    }

    public CountDTO getCounts() {
        CountDTO countDTO = new CountDTO();
        countDTO.setOrganizationCount(organizationRepository.count());
        countDTO.setApplicationCount(applicationRepository.count());
        return countDTO;
    }

    /**
     * Get organizations accessible by the user.
     * If user is Admin, returns all organizations.
     * Otherwise, returns only organizations where the user has permissions on at
     * least one application.
     */
    public List<OrganizationDTO> getOrganizationsForUser(String username, UserPermissionService userPermissionService) {
        if (userPermissionService.isAdmin(username)) {
            return getAllOrganizations();
        }

        // Get all permissions for the user
        List<UserPermission> permissions = userPermissionService.getPermissionsByUsername(username);

        // Extract unique organization IDs from permissions
        List<Long> organizationIds = permissions.stream()
                .map(p -> p.getOrganization() != null ? p.getOrganization().getId() : null)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        // Fetch and return organizations
        return organizationRepository.findAllById(organizationIds).stream()
                .map(organizationMapper::toDTO)
                .collect(Collectors.toList());
    }
}
