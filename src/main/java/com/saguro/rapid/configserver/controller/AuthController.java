package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.dto.LoginRequest;
import com.saguro.rapid.configserver.dto.LoginResponse;
import com.saguro.rapid.configserver.dto.PermissionSummaryDTO;
import com.saguro.rapid.configserver.entity.UserPermission;
import com.saguro.rapid.configserver.service.AuthService;
import com.saguro.rapid.configserver.service.UserPermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserPermissionService userPermissionService;

    public AuthController(AuthService authService, UserPermissionService userPermissionService) {
        this.authService = authService;
        this.userPermissionService = userPermissionService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.authenticate(
                loginRequest.getUsername(),
                loginRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionSummaryDTO>> getUserPermissions(Authentication authentication) {
        String username = authentication.getName();
        boolean isAdmin = userPermissionService.isAdmin(username);

        List<PermissionSummaryDTO> permissions = new ArrayList<>();
        if (!isAdmin) {
            List<UserPermission> userPermissions = userPermissionService.getPermissionsByUsername(username);
            permissions = userPermissions.stream()
                    .map(perm -> new PermissionSummaryDTO(
                            perm.getOrganization() != null ? perm.getOrganization().getId() : null,
                            perm.getOrganization() != null ? perm.getOrganization().getName() : null,
                            perm.getApplication() != null ? perm.getApplication().getId() : null,
                            perm.getApplication() != null ? perm.getApplication().getName() : null,
                            perm.isCanRead(),
                            perm.isCanCreate(),
                            perm.isCanUpdate(),
                            perm.isCanDelete()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(permissions);
    }
}
