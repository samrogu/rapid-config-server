package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.dto.ApplicationDTO;
import com.saguro.rapid.configserver.service.ApplicationService;
import com.saguro.rapid.configserver.service.UserPermissionService;
import com.saguro.rapid.configserver.entity.UserPermission;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserPermissionService userPermissionService;

    public ApplicationController(ApplicationService applicationService, UserPermissionService userPermissionService) {
        this.applicationService = applicationService;
        this.userPermissionService = userPermissionService;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Admin"));
    }

    @GetMapping
    public List<ApplicationDTO> getAllApplications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = getCurrentUsername();
        boolean hasRead = userPermissionService.getPermissionsByUsername(current)
                .stream()
                .anyMatch(UserPermission::isCanRead);

        if (!isAdmin(auth) && !hasRead) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return applicationService.getAllApplications();
    }

    @GetMapping("/{id}")
    public ApplicationDTO getApplicationById(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = getCurrentUsername();
        if (!isAdmin(auth) && !userPermissionService.canReadApplication(current, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return applicationService.getApplicationById(id);
    }

    @GetMapping("/organization/{organizationId}")
    public List<ApplicationDTO> getApplicationsByOrganization(@PathVariable("organizationId") Long organizationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = getCurrentUsername();
        if (!isAdmin(auth) && !userPermissionService.canReadOrganization(current, organizationId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return applicationService.getApplicationsByOrganization(organizationId);
    }

    @PostMapping("/organization/{organizationId}")
    public ApplicationDTO createApplication(@PathVariable("organizationId") Long organizationId, @RequestBody ApplicationDTO applicationDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = getCurrentUsername();
        if (!isAdmin(auth) && !userPermissionService.canCreateApplication(current, organizationId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return applicationService.createApplication(organizationId, applicationDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteApplication(@PathVariable("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = getCurrentUsername();
        if (!isAdmin(auth) && !userPermissionService.canDeleteApplication(current, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        applicationService.deleteApplication(id);
    }

    @PutMapping("/{id}")
    public ApplicationDTO updateApplication(@PathVariable("id") Long id, @RequestBody ApplicationDTO applicationDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = getCurrentUsername();
        if (!isAdmin(auth) && !userPermissionService.canUpdateApplication(current, id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return applicationService.updateApplication(id, applicationDTO);
    }

    @Operation(summary = "Get enabled applications by organization ID", description = "Retrieve all enabled applications for a specific organization.")
    @GetMapping("/enabled/{organizationId}")
    public List<ApplicationDTO> getEnabledApplicationsByOrganization(@PathVariable Long organizationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = getCurrentUsername();
        if (!isAdmin(auth) && !userPermissionService.canReadOrganization(current, organizationId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return applicationService.getEnabledApplicationsByOrganization(organizationId);
    }

    @Operation(summary = "Get enabled applications by organization ID and name", description = "Retrieve enabled applications for a specific organization filtered by name.")
    @GetMapping("/enabled/{organizationId}/{name}")
    public List<ApplicationDTO> getEnabledApplicationsByOrganizationAndName(@PathVariable Long organizationId, @PathVariable String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = getCurrentUsername();
        if (!isAdmin(auth) && !userPermissionService.canReadOrganization(current, organizationId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return applicationService.getEnabledApplicationsByOrganizationAndName(organizationId, name);
    }
}
