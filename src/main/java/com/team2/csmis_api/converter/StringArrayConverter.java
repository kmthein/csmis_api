package com.team2.csmis_api.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.AttributeConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringArrayConverter  implements AttributeConverter<String[], String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        try {
            return attribute != null ? objectMapper.writeValueAsString(attribute) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting array to JSON", e);
        }
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? objectMapper.readValue(dbData, String[].class) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON to array", e);
        }
    }

}
