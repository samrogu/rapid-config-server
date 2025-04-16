package com.saguro.rapid.configserver.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDTO {

    private Long id;
    private String name;
    private String description;
    private String uid; // UID único para identificar la organización
    @JsonInclude(JsonInclude.Include.NON_NULL) // Ignorar si es null
    private List<ApplicationDTO> applications;

}