package com.saguro.rapid.configserver.dto;

import lombok.Data;

/**
 * Data transfer object that represents the permissions of a user for a
 * particular organization and application. The boolean permission flags have
 * been grouped into {@link PermissionDTO} objects that will be serialised as
 * JSON when persisted or returned via the REST API.
 */

@Data
public class UserPermissionDTO {
    private Long id;
    private String username; // Nombre del usuario
    private Long organizationId; // ID de la organización
    private Long applicationId; // ID de la aplicación (puede ser null si aplica solo a la organización)

    private PermissionDTO organizationPermission;
    private PermissionDTO applicationPermission;
}
