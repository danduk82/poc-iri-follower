package org.danduk.retry.domain.serdes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

public class JsonNodeSerializer implements Serializer<JsonNode> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(final String s, final JsonNode data) {
        try {
            return data == null ? null : objectMapper.writeValueAsBytes(data);
        }
        catch (final Exception e) {
            throw new SerializationException("Error when serializing MessageDto to byte[]", e);
        }
    }

    @Override
    public byte[] serialize(final String topic, final Headers headers, final JsonNode data) {
        return Serializer.super.serialize(topic, headers, data);
    }

}

