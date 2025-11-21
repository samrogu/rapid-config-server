package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.dto.ApplicationDTO;
import com.saguro.rapid.configserver.service.ApplicationService;
import com.saguro.rapid.configserver.service.UserPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserPermissionService userPermissionService;

    public ApplicationController(ApplicationService applicationService,
            UserPermissionService userPermissionService) {
        this.applicationService = applicationService;
        this.userPermissionService = userPermissionService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ApplicationDTO> getAllApplications(Authentication authentication) {
        return applicationService.getApplicationsForUser(authentication.getName(), userPermissionService);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ApplicationDTO getApplicationById(@PathVariable Long id, Authentication authentication) {
        return applicationService.getApplicationByIdForUser(id, authentication.getName(), userPermissionService);
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('Admin') or @userPermissionService.canReadOrganization(authentication, #organizationId)")
    public List<ApplicationDTO> getApplicationsByOrganization(@PathVariable("organizationId") Long organizationId,
            Authentication authentication) {
        return applicationService.getApplicationsByOrganizationForUser(organizationId,
                authentication.getName(),
                userPermissionService);
    }

    @PostMapping("/organization/{organizationId}")
    @PreAuthorize("hasAuthority('Admin') or @userPermissionService.canCreateApplication(authentication, #organizationId)")
    public ApplicationDTO createApplication(@PathVariable("organizationId") Long organizationId,
            @RequestBody ApplicationDTO applicationDTO) {
        return applicationService.createApplication(organizationId, applicationDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin') or @userPermissionService.canDeleteApplication(authentication, #id)")
    public void deleteApplication(@PathVariable("id") Long id) {
        applicationService.deleteApplication(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin') or @userPermissionService.canUpdateApplication(authentication, #id)")
    public ApplicationDTO updateApplication(@PathVariable("id") Long id, @RequestBody ApplicationDTO applicationDTO) {
        return applicationService.updateApplication(id, applicationDTO);
    }

    @Operation(summary = "Get enabled applications by organization ID", description = "Retrieve all enabled applications for a specific organization.")
    @GetMapping("/enabled/{organizationId}")
    @PreAuthorize("hasAuthority('Admin') or @userPermissionService.canReadOrganization(authentication, #organizationId)")
    public List<ApplicationDTO> getEnabledApplicationsByOrganization(@PathVariable Long organizationId) {
        return applicationService.getEnabledApplicationsByOrganization(organizationId);
    }

    @Operation(summary = "Get enabled applications by organization ID and name", description = "Retrieve enabled applications for a specific organization filtered by name.")
    @GetMapping("/enabled/{organizationId}/{name}")
    @PreAuthorize("hasAuthority('Admin') or @userPermissionService.canReadOrganization(authentication, #organizationId)")
    public List<ApplicationDTO> getEnabledApplicationsByOrganizationAndName(@PathVariable Long organizationId,
            @PathVariable String name) {
        return applicationService.getEnabledApplicationsByOrganizationAndName(organizationId, name);
    }
}
