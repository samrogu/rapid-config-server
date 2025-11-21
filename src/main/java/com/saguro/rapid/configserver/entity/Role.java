package com.saguro.rapid.configserver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Nombre del rol (e.g., "ROLE_ADMIN", "ROLE_USER")

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isEditable = true; // Indica si el rol puede ser editado o eliminado
}
