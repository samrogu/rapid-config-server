package com.saguro.rapid.configserver.service;

import com.saguro.rapid.configserver.entity.Application;
import com.saguro.rapid.configserver.entity.Organization;
import com.saguro.rapid.configserver.repository.ApplicationRepository;
import com.saguro.rapid.configserver.repository.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final OrganizationRepository organizationRepository;

    public ApplicationService(ApplicationRepository applicationRepository, OrganizationRepository organizationRepository) {
        this.applicationRepository = applicationRepository;
        this.organizationRepository = organizationRepository;
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Application not found"));
    }

    public List<Application> getApplicationsByOrganization(Long organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        return organization.getApplications();
    }

    public Application createApplication(Long organizationId, Application application) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        application.setOrganization(organization);
        return applicationRepository.save(application);
    }

    public void deleteApplication(Long id) {
        applicationRepository.deleteById(id);
    }
}
