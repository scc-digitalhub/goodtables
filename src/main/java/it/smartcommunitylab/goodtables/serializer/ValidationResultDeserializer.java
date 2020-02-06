package it.smartcommunitylab.goodtables.serializer;

import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import it.smartcommunitylab.goodtables.model.ValidationResultDTO;
import it.smartcommunitylab.goodtables.model.ValidationStatus;

public class ValidationResultDeserializer extends StdDeserializer<ValidationResultDTO> {

    private static final long serialVersionUID = 7325201711034533646L;

    public ValidationResultDeserializer() {
        this(null);
    }

    public ValidationResultDeserializer(Class<?> t) {
        super(t);
    }

    @Override
    public ValidationResultDTO deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);
        ValidationResultDTO dto = new ValidationResultDTO();

        if (node.has("id")) {
            dto.setId(node.get("id").asLong());
        }
        if (node.has("kind")) {
            dto.setKind(node.get("kind").asText());
        }
        if (node.has("name")) {
            dto.setName(node.get("name").asText());
        }
        if (node.has("key")) {
            dto.setKey(node.get("key").asText());
        }
        if (node.has("type")) {
            dto.setType(node.get("type").asText());
        }
        if (node.has("status")) {
            dto.setStatus(node.get("status").asInt(ValidationStatus.UNKNOWN.value()));
        }

        JsonNode report = node.get("report");
        if (report != null) {
            dto.setReport(report.toString());
        }

        return dto;
    }
}