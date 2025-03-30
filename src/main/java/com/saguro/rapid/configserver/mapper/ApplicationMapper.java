package com.saguro.rapid.configserver.mapper;

import com.saguro.rapid.configserver.dto.ApplicationDTO;
import com.saguro.rapid.configserver.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    ApplicationMapper INSTANCE = Mappers.getMapper(ApplicationMapper.class);

    @Mapping(target = "organizationId", source = "organization.id") // Mapear el ID de la organización
    ApplicationDTO toDTO(Application application);

    @Mapping(target = "organization", ignore = true) // Ignorar la relación inversa al convertir de DTO a entidad
    Application toEntity(ApplicationDTO applicationDTO);
}