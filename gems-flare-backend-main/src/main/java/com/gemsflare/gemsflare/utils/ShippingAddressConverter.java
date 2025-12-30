package com.gemsflare.gemsflare.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemsflare.gemsflare.address.model.DeliveryAddressDTO;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ShippingAddressConverter implements AttributeConverter<DeliveryAddressDTO, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(DeliveryAddressDTO address) {
        try {
            return mapper.writeValueAsString(address);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert DeliveryAddressDTO to String", e);
        }
    }

    @Override
    public DeliveryAddressDTO convertToEntityAttribute(String json) {
        try {
            return mapper.readValue(json, DeliveryAddressDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert String to DeliveryAddressDTO", e);
        }
    }
}