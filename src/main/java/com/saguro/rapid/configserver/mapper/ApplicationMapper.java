package com.saguro.rapid.configserver.mapper;

import com.saguro.rapid.configserver.dto.ApplicationDTO;
import com.saguro.rapid.configserver.entity.Application;
import com.saguro.rapid.configserver.entity.User;
import com.saguro.rapid.configserver.entity.UserPermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ApplicationMapper {

    @Mapping(target = "permissions", source = "permissions")
    @Mapping(target = "organizationId", source = "organization.id")
    ApplicationDTO toDTO(Application application);

    @Mapping(target = "permissions", source = "permissions")
    @Mapping(target = "organization", ignore = true)
    Application toEntity(ApplicationDTO applicationDTO);

    // Método para mapear de List<UserPermission> a List<String>
    default List<String> mapPermissionsToStrings(List<UserPermission> permissions) {
        return permissions.stream()
                .map(permission -> permission.getUser().getUsername()) // Extraer el nombre de usuario desde la entidad User
                .collect(Collectors.toList());
    }

    // Método para mapear de List<String> a List<UserPermission>
    default List<UserPermission> mapStringsToPermissions(List<String> usernames) {
        return usernames.stream()
                .map(username -> {
                    User user = new User();
                    user.setUsername(username); // Crear una entidad User con el nombre de usuario
                    UserPermission permission = new UserPermission();
                    permission.setUser(user);
                    return permission;
                })
                .collect(Collectors.toList());
    }
}
