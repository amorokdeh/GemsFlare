package com.gemsflare.gemsflare.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemsflare.gemsflare.item.model.ItemDTO;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class ItemDTOListConverter implements AttributeConverter<List<ItemDTO>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ItemDTO> items) {
        try {
            return mapper.writeValueAsString(items);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert List<ItemDTO> to String", e);
        }
    }

    @Override
    public List<ItemDTO> convertToEntityAttribute(String json) {
        try {
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert String to List<ItemDTO>", e);
        }
    }
}
