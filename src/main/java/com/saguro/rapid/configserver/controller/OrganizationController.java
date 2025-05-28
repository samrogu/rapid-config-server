package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.dto.OrganizationDTO;
import com.saguro.rapid.configserver.dto.CountDTO;
import com.saguro.rapid.configserver.service.OrganizationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public List<OrganizationDTO> getAllOrganizations() {
        return organizationService.getAllOrganizations();
    }

    @GetMapping("/{id}")
    public OrganizationDTO getOrganizationById(@PathVariable("id") Long id) {
        return organizationService.getOrganizationById(id);
    }

    @PostMapping
    public OrganizationDTO createOrganization(@RequestBody OrganizationDTO organizationDTO) {
        return organizationService.createOrganization(organizationDTO);
    }

    @PutMapping("/{id}")
    public OrganizationDTO updateOrganization(@PathVariable("id") Long id, @RequestBody OrganizationDTO organizationDTO) {
        return organizationService.updateOrganization(id, organizationDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteOrganization(@PathVariable("id") Long id) {
        organizationService.deleteOrganization(id);
    }

    @GetMapping("/counts")
    public CountDTO getCounts() {
        return organizationService.getCounts();
    }
}
