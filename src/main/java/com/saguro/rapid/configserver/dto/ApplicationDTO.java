package com.saguro.rapid.configserver.dto;

import java.util.List;

import com.saguro.rapid.configserver.enums.VaultAuthMethod;

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
    private boolean vaultEnabled;
    private String vaultUsername; // Nombre de usuario para autenticación UserPass
    private String vaultPassword; // Contraseña para autenticación UserPass

    private Long organizationId; // Solo el ID de la organización
    private String uid; // UID único para identificar la aplicación
    private VaultAuthMethod vaultAuthMethod; 
    private List<String> permissions; // Lista de nombres de usuario

}