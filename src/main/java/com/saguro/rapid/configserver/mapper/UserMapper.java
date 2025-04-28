package com.saguro.rapid.configserver.mapper;

import com.saguro.rapid.configserver.dto.UserDTO;
import com.saguro.rapid.configserver.entity.Role;
import com.saguro.rapid.configserver.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    UserDTO toDTO(User user);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "stringsToRoles")
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
}
