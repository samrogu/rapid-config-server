package com.saguro.rapid.configserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.SecureRandom;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Column(unique = true, nullable = false, updatable = false, length = 6) // Limita el tamaño del UID a 6 caracteres
    private String uid; // UID único para identificar la organización

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
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