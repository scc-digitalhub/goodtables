package it.smartcommunitylab.goodtables.serializer;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import it.smartcommunitylab.goodtables.model.ValidationResultDTO;

public class ValidationResultSerializer extends StdSerializer<ValidationResultDTO> {

    private static final long serialVersionUID = 2765900697671425958L;

    public ValidationResultSerializer() {
        this(null);
    }

    public ValidationResultSerializer(Class<ValidationResultDTO> t) {
        super(t);
    }

    @Override
    public void serialize(
            ValidationResultDTO result, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        jgen.writeStartObject();
        jgen.writeNumberField("id", result.getId());
        // add date as iso format
        DateTime dateTime = new DateTime(result.getCreatedDate().getTime());
        jgen.writeStringField("createdDate", ISODateTimeFormat.dateTime().print(dateTime));
        jgen.writeStringField("kind", result.getKind());
        jgen.writeStringField("name", result.getName());
        jgen.writeStringField("key", result.getKey());
        jgen.writeStringField("type", result.getType());

        // write report json
        jgen.writeStringField("status", String.valueOf(result.getStatus()));

        if (!result.getReport().isEmpty()) {
            jgen.writeFieldName("report");
            jgen.writeRawValue(result.getReport());
        } else {
            jgen.writeStringField("report", "{}");
        }
        // close
        jgen.writeEndObject();
    }
}