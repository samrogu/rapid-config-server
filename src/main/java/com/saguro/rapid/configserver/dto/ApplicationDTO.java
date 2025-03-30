package com.saguro.rapid.configserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDTO {

    private Long id;
    private String name;
    private String description;

    private Long organizationId; // Organización a la que pertenece la aplicación
    // Información relacionada con Git y Vault
    private String uri;            // URI del repositorio Git
    private String profile;        // Perfil de configuración
    private String label;          // Rama o etiqueta en Git
    private boolean enabled;       // Si la aplicación está habilitada

    private String vaultUrl;       // URL del servidor Vault
    private String secretEngine;   // Motor de secretos (e.g., kv-v2)
    private String vaultToken;     // Token de autenticación para Vault
    private String appRoleId;      // Opcional: Role ID para AppRole
    private String appRoleSecret;  // Opcional: Secret ID para AppRole

}