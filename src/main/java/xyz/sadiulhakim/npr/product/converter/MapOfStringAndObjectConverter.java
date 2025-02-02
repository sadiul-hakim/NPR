package xyz.sadiulhakim.npr.product.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

@Converter(autoApply = true)
public class MapOfStringAndObjectConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(MapOfStringAndObjectConverter.class);

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception ex) {
            LOGGER.error("MapOfStringAndObjectConverter.convertToDatabaseColumn :: Error occurred {}", ex.getMessage());
            return "";
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return MAPPER.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception ex) {
            LOGGER.error("MapOfStringAndObjectConverter.convertToEntityAttribute :: Error occurred {}", ex.getMessage());
            return Collections.emptyMap();
        }
    }
}
