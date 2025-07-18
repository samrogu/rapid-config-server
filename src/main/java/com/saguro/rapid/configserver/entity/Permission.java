package com.saguro.rapid.configserver.entity;

import lombok.Data;

/**
 * Simple POJO representing a set of permission flags. Instances of this class
 * are persisted as JSON via {@link com.saguro.rapid.configserver.converter.PermissionConverter}.
 */
@Data
public class Permission {
    private boolean canManage;
    private boolean canRead;
    private boolean canCreate;
    private boolean canUpdate;
    private boolean canDelete;
}
