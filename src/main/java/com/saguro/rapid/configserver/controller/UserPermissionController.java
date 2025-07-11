package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.dto.UserPermissionDTO;
import com.saguro.rapid.configserver.entity.UserPermission;
import com.saguro.rapid.configserver.mapper.UserPermissionMapper;
import com.saguro.rapid.configserver.service.UserPermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/permissions")
public class UserPermissionController {

    private final UserPermissionService userPermissionService;
    private final UserPermissionMapper userPermissionMapper;

    public UserPermissionController(UserPermissionService userPermissionService, UserPermissionMapper userPermissionMapper) {
        this.userPermissionService = userPermissionService;
        this.userPermissionMapper = userPermissionMapper;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Admin"));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<UserPermissionDTO>> getPermissionsByUsername(@PathVariable("username") String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = getCurrentUsername();
        if (!isAdmin(auth) && !username.equals(current)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        List<UserPermission> permissions = userPermissionService.getPermissionsByUsername(username);
        List<UserPermissionDTO> permissionDTOs = permissions.stream()
                .map(userPermissionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionDTOs);
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<UserPermission>> getPermissionsByOrganization(@PathVariable("organizationId") Long organizationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = getCurrentUsername();
        if (!isAdmin(auth) && !userPermissionService.hasOrganizationPermission(current, organizationId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(userPermissionService.getPermissionsByOrganization(organizationId));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<UserPermission>> getPermissionsByApplication(@PathVariable("applicationId") Long applicationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String current = getCurrentUsername();
        if (!isAdmin(auth) && !userPermissionService.hasApplicationPermission(current, applicationId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(userPermissionService.getPermissionsByApplication(applicationId));
    }
    
    @PostMapping("/create")
    public ResponseEntity<UserPermissionDTO> createPermission(@RequestBody UserPermissionDTO userPermissionDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        UserPermission savedPermission = userPermissionService.createPermission(userPermissionDTO);
        return ResponseEntity.ok(userPermissionMapper.toDTO(savedPermission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        userPermissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserPermissionDTO>> getAllPermissions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!isAdmin(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        List<UserPermission> permissions = userPermissionService.getAllPermissions();
        List<UserPermissionDTO> permissionDTOs = permissions.stream()
                .map(userPermissionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionDTOs);
    }
}
