package com.saguro.rapid.configserver.mapper;

import com.saguro.rapid.configserver.dto.UserDTO;
import com.saguro.rapid.configserver.entity.Role;
import com.saguro.rapid.configserver.entity.User;
import com.saguro.rapid.configserver.entity.UserPermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "permissionsToIds")
    UserDTO toDTO(User user);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "stringsToRoles")
    @Mapping(target = "permissions", source = "permissions", qualifiedByName = "idsToPermissions")
    User toEntity(UserDTO userDTO);

    List<UserDTO> toDTOList(List<User> users);

    List<User> toEntityList(List<UserDTO> userDTOs);

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName) // Extraer el nombre del rol
                .collect(Collectors.toSet());
    }

    @Named("stringsToRoles")
    default Set<Role> stringsToRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(roleName); // Crear un objeto Role con el nombre
                    return role;
                })
                .collect(Collectors.toSet());
    }

    @Named("permissionsToIds")
    default Set<Long> permissionsToIds(List<UserPermission> permissions) {
        if (permissions == null) return null;
        return permissions.stream().map(UserPermission::getId).collect(Collectors.toSet());
    }

    @Named("idsToPermissions")
    default List<UserPermission> idsToPermissions(Set<Long> ids) {
        if (ids == null) return null;
        return ids.stream().map(id -> {
            UserPermission up = new UserPermission();
            up.setId(id);
            return up;
        }).collect(Collectors.toList());
    }
}
