package com.saguro.rapid.configserver.controller;

import org.springframework.cloud.config.environment.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saguro.rapid.configserver.components.DynamicConfigComponent;

@RestController
@RequestMapping("/config/v1")
public class ConfigController {

    private final DynamicConfigComponent dynamicConfigComponent;
    private final com.saguro.rapid.configserver.service.ApplicationService applicationService;
    private final com.saguro.rapid.configserver.service.UserPermissionService userPermissionService;

    public ConfigController(DynamicConfigComponent dynamicConfigComponent,
            com.saguro.rapid.configserver.service.ApplicationService applicationService,
            com.saguro.rapid.configserver.service.UserPermissionService userPermissionService) {
        this.dynamicConfigComponent = dynamicConfigComponent;
        this.applicationService = applicationService;
        this.userPermissionService = userPermissionService;
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ConfigController.class);

    @GetMapping("/{org}/{aplicacion}/{microservices}/{profile}/{label}")
    public Environment getConfig(
            @PathVariable("org") String organization,
            @PathVariable("aplicacion") String application,
            @PathVariable("microservices") String microservice,
            @PathVariable("profile") String profile,
            @PathVariable("label") String label,
            org.springframework.security.core.Authentication authentication) {

        String username = authentication.getName();
        logger.info("Config request for {}/{}/{} (label: {}) by user: {}", organization, application, microservice,
                label, username);

        // 1. Resolver la aplicación para obtener su ID
        // CORRECCIÓN: Usar 'label' en lugar de 'microservice' para buscar la
        // aplicación,
        // coincidiendo con la lógica de DynamicConfigComponent.
        var appOpt = applicationService.findByOrganizationAndUidAndMicroservice(organization, application, label);

        if (appOpt.isEmpty()) {
            logger.warn("Application not found for lookup: {}/{}/{}", organization, application, label);
            // Si la app no existe en nuestra BD, delegamos a DynamicConfigComponent (que
            // probablemente fallará o retornará null)
            // pero no bloqueamos por permisos ya que no hay ID contra el cual verificar.
            return dynamicConfigComponent.findOne(String.join("/", organization, application, microservice), profile,
                    label);
        }

        Long applicationId = appOpt.get().getId();
        logger.debug("Application resolved to ID: {}", applicationId);

        // 2. Verificar permisos
        boolean isAdmin = userPermissionService.isAdmin(username);
        boolean canRead = userPermissionService.canReadApplication(authentication, applicationId);

        logger.info("Permission check - User: {}, IsAdmin: {}, CanReadApp({}): {}", username, isAdmin, applicationId,
                canRead);

        if (!isAdmin && !canRead) {
            logger.warn("Access denied for user {} to application ID {}", username, applicationId);
            throw new org.springframework.security.access.AccessDeniedException("Access is denied");
        }

        // Usamos String.join para unir las partes de manera elegante
        String fullApplicationPath = String.join("/", organization, application, microservice);

        // Retornar la lista filtrada
        return dynamicConfigComponent.findOne(fullApplicationPath, profile, label);
    }
}