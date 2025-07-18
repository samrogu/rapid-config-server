package com.saguro.rapid.configserver.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saguro.rapid.configserver.entity.Permission;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA converter to store {@link Permission} objects as JSON strings in the database.
 */
@Converter(autoApply = false)
public class PermissionConverter implements AttributeConverter<Permission, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Permission attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error serializing Permission", e);
        }
    }

    @Override
    public Permission convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return mapper.readValue(dbData, Permission.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error deserializing Permission", e);
        }
    }
}
