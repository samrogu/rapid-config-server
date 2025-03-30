package com.saguro.rapid.configserver.mapper;

import com.saguro.rapid.configserver.dto.OrganizationDTO;
import com.saguro.rapid.configserver.entity.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);

    OrganizationDTO toDTO(Organization organization);

    Organization toEntity(OrganizationDTO organizationDTO);
}