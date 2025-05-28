package com.saguro.rapid.configserver.controller;

import com.saguro.rapid.configserver.dto.UserPermissionDTO;
import com.saguro.rapid.configserver.entity.UserPermission;
import com.saguro.rapid.configserver.mapper.UserPermissionMapper;
import com.saguro.rapid.configserver.service.UserPermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permissions")
public class UserPermissionController {

    private final UserPermissionService userPermissionService;
    private final UserPermissionMapper userPermissionMapper;

    public UserPermissionController(UserPermissionService userPermissionService, UserPermissionMapper userPermissionMapper) {
        this.userPermissionService = userPermissionService;
        this.userPermissionMapper = userPermissionMapper;
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<UserPermissionDTO>> getPermissionsByUsername(@PathVariable("username") String username) {
        List<UserPermission> permissions = userPermissionService.getPermissionsByUsername(username);
        List<UserPermissionDTO> permissionDTOs = permissions.stream()
                .map(userPermissionMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissionDTOs);
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<UserPermission>> getPermissionsByOrganization(@PathVariable("organizationId") Long organizationId) {
        return ResponseEntity.ok(userPermissionService.getPermissionsByOrganization(organizationId));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<UserPermission>> getPermissionsByApplication(@PathVariable("organizationId") Long applicationId) {
        return ResponseEntity.ok(userPermissionService.getPermissionsByApplication(applicationId));
    }

    @PostMapping
    public ResponseEntity<UserPermissionDTO> createPermission(@RequestBody UserPermissionDTO userPermissionDTO) {
        UserPermission userPermission = userPermissionMapper.toEntity(userPermissionDTO);
        UserPermission savedPermission = userPermissionService.savePermission(userPermission);
        return ResponseEntity.ok(userPermissionMapper.toDTO(savedPermission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") Long id) {
        userPermissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
