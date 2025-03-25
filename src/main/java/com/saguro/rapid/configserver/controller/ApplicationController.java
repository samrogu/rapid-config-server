package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.entity.Application;
import com.saguro.rapid.configserver.service.ApplicationService;
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
    public List<Application> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @GetMapping("/{id}")
    public Application getApplicationById(@PathVariable Long id) {
        return applicationService.getApplicationById(id);
    }

    @GetMapping("/organization/{organizationId}")
    public List<Application> getApplicationsByOrganization(@PathVariable Long organizationId) {
        return applicationService.getApplicationsByOrganization(organizationId);
    }

    @PostMapping("/organization/{organizationId}")
    public Application createApplication(@PathVariable Long organizationId, @RequestBody Application application) {
        return applicationService.createApplication(organizationId, application);
    }

    @DeleteMapping("/{id}")
    public void deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
    }
}
