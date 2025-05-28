package com.saguro.rapid.configserver.dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String password; // Opcional: puedes omitirlo en respuestas si es sensible
    private Set<String> roles; // Los roles se manejan como nombres de roles
}
