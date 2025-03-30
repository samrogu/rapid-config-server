package com.saguro.rapid.configserver.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    
}
