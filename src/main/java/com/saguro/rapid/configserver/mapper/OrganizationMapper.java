package com.saguro.rapid.configserver.mapper;

import com.saguro.rapid.configserver.dto.OrganizationDTO;
import com.saguro.rapid.configserver.entity.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = ApplicationMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR) // Usar ApplicationMapper
public interface OrganizationMapper {

    // Mapeo de Organization a OrganizationDTO
    @Mapping(target = "applications", source = "applications") // Mapear la lista de aplicaciones
    OrganizationDTO toDTO(Organization organization);

    // Mapeo de OrganizationDTO a Organization
    @Mapping(target = "applications", ignore = true) // Ignorar la lista al convertir de DTO a entidad
    Organization toEntity(OrganizationDTO organizationDTO);
}
