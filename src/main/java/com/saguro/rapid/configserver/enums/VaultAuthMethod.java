package com.saguro.rapid.configserver.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VaultAuthMethod {
    TOKEN,
    APPROLE,
    USERPASS;

    @JsonCreator
    public static VaultAuthMethod from(String value) {
        if (value == null || value.isBlank()) {
            return null; // treat empty or blank as null
        }
        try {
            return VaultAuthMethod.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // unknown value, return null to let validation handle it
            return null;
        }
    }
}
