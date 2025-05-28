package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.dto.ApplicationDTO;
import com.saguro.rapid.configserver.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public List<ApplicationDTO> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @GetMapping("/{id}")
    public ApplicationDTO getApplicationById(@PathVariable Long id) {
        return applicationService.getApplicationById(id);
    }

    @GetMapping("/organization/{organizationId}")
    public List<ApplicationDTO> getApplicationsByOrganization(@PathVariable("organizationId") Long organizationId) {
        return applicationService.getApplicationsByOrganization(organizationId);
    }

    @PostMapping("/organization/{organizationId}")
    public ApplicationDTO createApplication(@PathVariable("organizationId") Long organizationId, @RequestBody ApplicationDTO applicationDTO) {
        return applicationService.createApplication(organizationId, applicationDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteApplication(@PathVariable("id") Long id) {
        applicationService.deleteApplication(id);
    }

    @PutMapping("/{id}")
    public ApplicationDTO updateApplication(@PathVariable("id") Long id, @RequestBody ApplicationDTO applicationDTO) {
        return applicationService.updateApplication(id, applicationDTO);
    }

    @Operation(summary = "Get enabled applications by organization ID", description = "Retrieve all enabled applications for a specific organization.")
    @GetMapping("/enabled/{organizationId}")
    public List<ApplicationDTO> getEnabledApplicationsByOrganization(@PathVariable Long organizationId) {
        return applicationService.getEnabledApplicationsByOrganization(organizationId);
    }

    @Operation(summary = "Get enabled applications by organization ID and name", description = "Retrieve enabled applications for a specific organization filtered by name.")
    @GetMapping("/enabled/{organizationId}/{name}")
    public List<ApplicationDTO> getEnabledApplicationsByOrganizationAndName(@PathVariable Long organizationId, @PathVariable String name) {
        return applicationService.getEnabledApplicationsByOrganizationAndName(organizationId, name);
    }
}
