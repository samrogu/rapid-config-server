package com.saguro.rapid.configserver.dto;

import lombok.Data;

@Data
public class UserPermissionDTO {
    private Long id;
    private String username; // Nombre del usuario
    private Long organizationId; // ID de la organización
    private Long applicationId; // ID de la aplicación (puede ser null si aplica solo a la organización)

    private boolean canRead;
    private boolean canCreate;
    private boolean canUpdate;
    private boolean canDelete;
}
