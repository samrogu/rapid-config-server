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

    public ConfigController(DynamicConfigComponent dynamicConfigComponent) {
        this.dynamicConfigComponent = dynamicConfigComponent;
    }

    @GetMapping("/{org}/{aplicacion}/{microservices}/{profile}/{label}")
    public Environment getConfig(
            @PathVariable("org") String organization,
            @PathVariable("aplicacion") String application,
            @PathVariable("microservices") String microservice,
            @PathVariable("profile") String profile,
            @PathVariable("label") String label) {
        
        // Usamos String.join para unir las partes de manera elegante
        String fullApplicationPath = String.join("/", organization, application, microservice);
        
        // Retornar la lista filtrada
        return dynamicConfigComponent.findOne(fullApplicationPath, profile, label);
    }
}