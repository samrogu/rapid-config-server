package com.saguro.rapid.configserver.entity;

import com.saguro.rapid.configserver.converter.PermissionConverter;
import jakarta.persistence.*;
import lombok.Data;
import com.saguro.rapid.configserver.entity.Permission;

@Data
@Entity
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Relación con la entidad User
    private User user; // Usuario al que se asignan los permisos

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = true)
    private Application application;

    @Convert(converter = PermissionConverter.class)
    @Column(columnDefinition = "TEXT")
    private Permission organizationPermission;

    @Convert(converter = PermissionConverter.class)
    @Column(columnDefinition = "TEXT")
    private Permission applicationPermission;
}
