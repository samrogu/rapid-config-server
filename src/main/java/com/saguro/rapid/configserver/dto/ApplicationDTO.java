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
    private String uri;
    private String profile;
    private String label;
    private boolean enabled;
    private String vaultUrl;
    private String secretEngine;
    private String vaultToken;
    private String appRoleId;
    private String appRoleSecret;

    private Long organizationId; // Solo el ID de la organización

    public Long getOrganizationId() {
        return organizationId;
    }
    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

}