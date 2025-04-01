package com.saguro.rapid.configserver.mapper;

import com.saguro.rapid.configserver.dto.ApplicationDTO;
import com.saguro.rapid.configserver.entity.Application;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Usamos el OrganizationMapper aquí
public interface ApplicationMapper {

    ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

    // Mapeo de Application a ApplicationDTO
    ApplicationDTO toDTO(Application application);

    // Mapeo de ApplicationDTO a Application
    Application toEntity(ApplicationDTO applicationDTO);
}
