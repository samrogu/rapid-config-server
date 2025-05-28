package com.saguro.rapid.configserver.mapper;

import com.saguro.rapid.configserver.dto.RoleDTO;
import com.saguro.rapid.configserver.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO toDTO(Role role);

    Role toEntity(RoleDTO roleDTO);

    List<RoleDTO> toDTOList(List<Role> roles);

    List<Role> toEntityList(List<RoleDTO> roleDTOs);
}
