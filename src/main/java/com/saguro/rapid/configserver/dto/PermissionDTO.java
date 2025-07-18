package com.saguro.rapid.configserver.dto;

import lombok.Data;

@Data
public class PermissionDTO {
    private boolean canManage;
    private boolean canRead;
    private boolean canCreate;
    private boolean canUpdate;
    private boolean canDelete;
}
