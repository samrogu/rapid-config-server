package com.saguro.rapid.configserver.entity;

import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class GitRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String organization;
    private String application;
    private String microservice;
    private String uri;
    private String profile;
    private String label;
    private boolean enabled;

    // Campos específicos para Vault
    private String vaultUrl;       // URL del servidor Vault
    private String secretEngine;   // Motor de secretos (e.g., kv-v2)
    private String vaultToken;     // Token de autenticación para Vault
    private String appRoleId;      // Opcional: Role ID para AppRole
    private String appRoleSecret;  // Opcional: Secret ID para AppRole
    
    public GitRepository() {
        // No-argument constructor
    }

    public GitRepository(Long id, String organization, String application, String microservice, String uri, String profile, String label, boolean enabled, String vaultUrl, String secretEngine, String vaultToken, String appRoleId, String appRoleSecret) {
        this.id = id;
        this.organization = organization;
        this.application = application;
        this.microservice = microservice;
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

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getMicroservice() {
        return microservice;
    }

    public void setMicroservice(String microservice) {
        this.microservice = microservice;
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
