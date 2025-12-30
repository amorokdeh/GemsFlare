package com.gemsflare.gemsflare.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Converter(autoApply = true)
public class UUIDListConverter implements AttributeConverter<List<UUID>, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<UUID> uuidList) {
        try {
            return objectMapper.writeValueAsString(uuidList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert UUID List to JSON", e);
        }
    }

    @Override
    public List<UUID> convertToEntityAttribute(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<UUID>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to UUID List", e);
        }
    }
}