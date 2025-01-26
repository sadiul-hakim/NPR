package xyz.sadiulhakim.npr.product.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Converter
public class ListOfLongConverter implements AttributeConverter<List<Long>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(ListOfLongConverter.class);

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception ex) {
            LOGGER.error("ListOfLongConverter.convertToDatabaseColumn :: Error occurred {}", ex.getMessage());
            return "";
        }
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        try {
            return MAPPER.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception ex) {
            LOGGER.error("ListOfLongConverter.convertToEntityAttribute :: Error occurred {}", ex.getMessage());
            return Collections.emptyList();
        }
    }
}
