package com.saguro.rapid.configserver.mapper;

import com.saguro.rapid.configserver.dto.UserPermissionDTO;
import com.saguro.rapid.configserver.entity.UserPermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserPermissionMapper {

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "applicationId", source = "application.id")
    @Mapping(target = "canRead", source = "canRead")
    @Mapping(target = "canCreate", source = "canCreate")
    @Mapping(target = "canUpdate", source = "canUpdate")
    @Mapping(target = "canDelete", source = "canDelete")
    UserPermissionDTO toDTO(UserPermission userPermission);

    @Mapping(target = "user.username", source = "username")
    @Mapping(target = "organization.id", source = "organizationId")
    @Mapping(target = "application.id", source = "applicationId")
    @Mapping(target = "canRead", source = "canRead")
    @Mapping(target = "canCreate", source = "canCreate")
    @Mapping(target = "canUpdate", source = "canUpdate")
    @Mapping(target = "canDelete", source = "canDelete")
    UserPermission toEntity(UserPermissionDTO userPermissionDTO);
}
