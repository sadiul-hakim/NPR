package xyz.sadiulhakim.npr.product.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Converter(autoApply = true)
public class SetOfLongConverter implements AttributeConverter<Set<Long>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(SetOfLongConverter.class);

    @Override
    public String convertToDatabaseColumn(Set<Long> attribute) {
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception ex) {
            LOGGER.error("ListOfLongConverter.convertToDatabaseColumn :: Error occurred {}", ex.getMessage());
            return "";
        }
    }

    @Override
    public Set<Long> convertToEntityAttribute(String dbData) {
        try {
            return MAPPER.readValue(dbData, new TypeReference<>() {
            });
        } catch (Exception ex) {
            LOGGER.error("ListOfLongConverter.convertToEntityAttribute :: Error occurred {}", ex.getMessage());
            return Collections.emptySet();
        }
    }
}
