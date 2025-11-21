package com.saguro.rapid.configserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.saguro.rapid.configserver.enums.VaultAuthMethod;

import java.security.SecureRandom;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(unique = true, nullable = false, updatable = false, length = 6) // Limita el tamaño del UID a 6 caracteres
    private String uid; // UID único para identificar la aplicación

    // Información relacionada con Git y Vault
    private String uri; // URI del repositorio Git
    private String profile; // Perfil de configuración
    private String label; // Rama o etiqueta en Git
    private boolean enabled; // Si la aplicación está habilitada

    // Campos relacionados con Vault
    private String vaultUrl; // URL del servidor Vault
    private String secretEngine; // Motor de secretos (e.g., kv-v2)
    private String vaultToken; // Token de autenticación para Vault
    private String appRoleId; // Opcional: Role ID para AppRole
    private String appRoleSecret; // Opcional: Secret ID para AppRole
    @Column(columnDefinition = "boolean default false")
    private boolean vaultEnabled;

    @Enumerated(EnumType.STRING)
    private VaultAuthMethod vaultAuthMethod; // Método de autenticación para Vault

    // Campos adicionales para autenticación por usuario y contraseña
    private String vaultUsername; // Nombre de usuario para autenticación UserPass
    private String vaultPassword; // Contraseña para autenticación UserPass

    private String vaultSchema; // http o https
    private Integer vaultPort; // Puerto del servidor Vault

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserPermission> permissions;

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int UID_LENGTH = 6;

    @PrePersist
    private void assignUidIfAbsent() {
        if (this.uid == null || this.uid.isEmpty()) {
            this.uid = generateShortUid();
        }
    }

    private String generateShortUid() {
        SecureRandom random = new SecureRandom();
        StringBuilder uidBuilder = new StringBuilder(UID_LENGTH);
        for (int i = 0; i < UID_LENGTH; i++) {
            uidBuilder.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return uidBuilder.toString();
    }
}