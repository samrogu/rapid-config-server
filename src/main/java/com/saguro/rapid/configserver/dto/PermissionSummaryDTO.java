package com.saguro.rapid.configserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionSummaryDTO {
    private Long organizationId;
    private String organizationName;
    private Long applicationId;
    private String applicationName;
    private boolean canRead;
    private boolean canCreate;
    private boolean canUpdate;
    private boolean canDelete;
}
