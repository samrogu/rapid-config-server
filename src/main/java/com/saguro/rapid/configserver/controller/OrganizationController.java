package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.dto.OrganizationDTO;
import com.saguro.rapid.configserver.dto.CountDTO;
import com.saguro.rapid.configserver.service.OrganizationService;
import com.saguro.rapid.configserver.service.UserPermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;
    private final UserPermissionService userPermissionService;

    public OrganizationController(OrganizationService organizationService,
            UserPermissionService userPermissionService) {
        this.organizationService = organizationService;
        this.userPermissionService = userPermissionService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<OrganizationDTO> getAllOrganizations(Authentication authentication) {
        return organizationService.getOrganizationsForUser(authentication.getName(), userPermissionService);
    }

    @GetMapping("/{id}")
    public OrganizationDTO getOrganizationById(@PathVariable("id") Long id, Authentication authentication) {
        if (!userPermissionService.isAdmin(authentication.getName()) &&
                !userPermissionService.canReadOrganization(authentication, id)) {
            throw new org.springframework.security.access.AccessDeniedException("Access is denied");
        }
        return organizationService.getOrganizationById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('Admin')")
    public OrganizationDTO createOrganization(@RequestBody OrganizationDTO organizationDTO) {
        return organizationService.createOrganization(organizationDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public OrganizationDTO updateOrganization(@PathVariable("id") Long id,
            @RequestBody OrganizationDTO organizationDTO) {
        return organizationService.updateOrganization(id, organizationDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('Admin')")
    public void deleteOrganization(@PathVariable("id") Long id) {
        organizationService.deleteOrganization(id);
    }

    @GetMapping("/counts")
    @PreAuthorize("hasAuthority('Admin')")
    public CountDTO getCounts() {
        return organizationService.getCounts();
    }

    @GetMapping("/debug/can-read/{id}")
    @PreAuthorize("isAuthenticated()")
    public String debugCanRead(@PathVariable("id") Long id, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String principalClass = principal.getClass().getName();
        String principalString = principal.toString();

        String username = authentication.getName();
        boolean isAdmin = userPermissionService.isAdmin(username);
        boolean canRead = userPermissionService.canReadOrganization(username, id);

        return String.format("Principal Class: %s, Principal: %s, User: %s, IsAdmin: %s, CanReadOrg(%d): %s",
                principalClass, principalString, username, isAdmin, id, canRead);
    }
}
