package com.saguro.rapid.configserver.service;

import com.saguro.rapid.configserver.dto.ApplicationDTO;
import com.saguro.rapid.configserver.entity.Application;
import com.saguro.rapid.configserver.entity.Organization;
import com.saguro.rapid.configserver.mapper.ApplicationMapper;
import com.saguro.rapid.configserver.repository.ApplicationRepository;
import com.saguro.rapid.configserver.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;
    private final OrganizationRepository organizationRepository;

    public ApplicationService(ApplicationRepository applicationRepository, ApplicationMapper applicationMapper, OrganizationRepository organizationRepository) {
        this.applicationRepository = applicationRepository;
        this.applicationMapper = applicationMapper;
        this.organizationRepository = organizationRepository;
    }

    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(application -> applicationMapper.toDTO(application))
                .collect(Collectors.toList());
    }

    public ApplicationDTO getApplicationById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));
        return applicationMapper.toDTO(application);
    }

    public List<ApplicationDTO> getApplicationsByOrganization(Long organizationId) {
        return applicationRepository.findByOrganizationId(organizationId).stream().map(applicationMapper::toDTO)
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

    public List<ApplicationDTO> getEnabledApplicationsByOrganization(Long organizationId) {
        return applicationRepository.findByEnabledTrueAndOrganizationId(organizationId).stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ApplicationDTO> getEnabledApplicationsByOrganizationAndName(Long organizationId, String name) {
        return applicationRepository.findByEnabledTrueAndOrganizationIdAndName(organizationId, name).stream()
                .map(applicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ApplicationDTO updateApplication(Long id, ApplicationDTO applicationDTO) {
        // Buscar la aplicación existente
        Application existingApplication = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        // Actualizar los campos de la aplicación existente
        existingApplication.setName(applicationDTO.getName());
        existingApplication.setDescription(applicationDTO.getDescription());
        existingApplication.setUri(applicationDTO.getUri());
        existingApplication.setProfile(applicationDTO.getProfile());
        existingApplication.setLabel(applicationDTO.getLabel());
        existingApplication.setEnabled(applicationDTO.isEnabled());
        existingApplication.setVaultUrl(applicationDTO.getVaultUrl());
        existingApplication.setSecretEngine(applicationDTO.getSecretEngine());
        existingApplication.setVaultToken(applicationDTO.getVaultToken());
        existingApplication.setAppRoleId(applicationDTO.getAppRoleId());
        existingApplication.setAppRoleSecret(applicationDTO.getAppRoleSecret());

        // Guardar los cambios en la base de datos
        Application updatedApplication = applicationRepository.save(existingApplication);

        // Convertir la entidad actualizada a DTO y devolverla
        return applicationMapper.toDTO(updatedApplication);
    }
}
