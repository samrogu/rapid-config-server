package com.saguro.rapid.configserver.mapper;

import com.saguro.rapid.configserver.dto.ApplicationDTO;
import com.saguro.rapid.configserver.entity.Application;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = OrganizationMapper.class, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ApplicationMapper {

    // Mapeo de Application a ApplicationDTO
    @Mapping(target = "organizationId", source = "organization.id") // Mapear solo el ID de la organización
    ApplicationDTO toDTO(Application application);

    // Mapeo de ApplicationDTO a Application
    @Mapping(target = "organization", ignore = true) // Ignorar la relación inversa
    Application toEntity(ApplicationDTO applicationDTO);
}
