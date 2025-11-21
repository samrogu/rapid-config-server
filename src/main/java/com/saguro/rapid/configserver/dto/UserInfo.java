package com.saguro.rapid.configserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Long id;
    private String username;
    private Set<String> roles;
    private boolean isAdmin;
    private List<PermissionSummaryDTO> permissions;
}
