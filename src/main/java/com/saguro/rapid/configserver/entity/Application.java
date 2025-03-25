// filepath: /src/main/java/com/saguro/rapid/configserver/entity/Application.java
package com.saguro.rapid.configserver.entity;

import jakarta.persistence.*;

@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

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

    public Application() {
    }

    public Application(String name, String description, Organization organization, String uri, String profile, String label, boolean enabled, String vaultUrl, String secretEngine, String vaultToken, String appRoleId, String appRoleSecret) {
        this.name = name;
        this.description = description;
        this.organization = organization;
        this.uri = uri;
        this.profile = profile;
        this.label = label;
        this.enabled = enabled;
        this.vaultUrl = vaultUrl;
        this.secretEngine = secretEngine;
        this.vaultToken = vaultToken;
        this.appRoleId = appRoleId;
        this.appRoleSecret = appRoleSecret;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVaultUrl() {
        return vaultUrl;
    }

    public void setVaultUrl(String vaultUrl) {
        this.vaultUrl = vaultUrl;
    }

    public String getSecretEngine() {
        return secretEngine;
    }

    public void setSecretEngine(String secretEngine) {
        this.secretEngine = secretEngine;
    }

    public String getVaultToken() {
        return vaultToken;
    }

    public void setVaultToken(String vaultToken) {
        this.vaultToken = vaultToken;
    }

    public String getAppRoleId() {
        return appRoleId;
    }

    public void setAppRoleId(String appRoleId) {
        this.appRoleId = appRoleId;
    }

    public String getAppRoleSecret() {
        return appRoleSecret;
    }

    public void setAppRoleSecret(String appRoleSecret) {
        this.appRoleSecret = appRoleSecret;
    }
}