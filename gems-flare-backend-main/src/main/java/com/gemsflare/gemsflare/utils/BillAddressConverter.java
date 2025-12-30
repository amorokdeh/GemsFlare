package com.gemsflare.gemsflare.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemsflare.gemsflare.address.model.BillAddressDTO;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BillAddressConverter implements AttributeConverter<BillAddressDTO, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(BillAddressDTO address) {
        try {
            return mapper.writeValueAsString(address);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert BillAddressDTO to String", e);
        }
    }

    @Override
    public BillAddressDTO convertToEntityAttribute(String json) {
        try {
            return mapper.readValue(json, BillAddressDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert String to BillAddressDTO", e);
        }
    }
}