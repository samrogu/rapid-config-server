package com.saguro.rapid.configserver.entity;

import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class GitRepository {

    @Id
    private Long id;
    private String organization;
    private String application;
    private String microservice;
    private String uri;
    private String profile;
    private String label;
    private boolean enabled;
    
    public GitRepository() {
        // No-argument constructor
    }

    public GitRepository(Long id, String organization, String application, String microservice, String uri, String profile, String label, boolean enabled) {
        this.id = id;
        this.organization = organization;
        this.application = application;
        this.microservice = microservice;
        this.uri = uri;
        this.profile = profile;
        this.label = label;
        this.enabled = enabled;
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
}
