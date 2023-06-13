package org.danduk.retry.domain;

import java.util.Arrays;
import java.util.Objects;

import org.danduk.retry.domain.dto.DebeziumOperation;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;


/**
 * Helper class to retrieve values from Kafka messages.
 */
public class KafkaMessageUtils {

    @SuppressWarnings("java:S1075")
    static final String PAYLOAD_PATH = "/payload";

    /**
     * <p>Retrieves the id from the given {@link JsonNode}.</p>
     * <p>Throws an {@link IllegalArgumentException} if no node with name '{@code id}' can be found in the message.</p>
     */
    public static long getId(final JsonNode jsonNode) {
        return getNodeAtIn("/after/id", jsonNode).longValue();
    }

    public static long getBeforeId(final JsonNode jsonNode) {
        return getNodeAtIn("/before/id", jsonNode).longValue();
    }

    public static DebeziumOperation getDebeziumOperation(final JsonNode jsonNode) {
        final String opValue = getNodeAtIn("/op", jsonNode).asText();
        return Arrays.stream(DebeziumOperation.values()).filter(op -> op.getValue().equals(opValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Unknown Debezium operation '%s'", opValue)));
    }

    /**
     * <p>Retrieves the status from the given {@link JsonNode}.</p>
     * <p>Throws an {@link IllegalArgumentException} if no node with name '{@code status}' can be found in the message.</p>
     */
    public static String getStatus(final JsonNode jsonNode) {
        return getNodeAtIn("/after/status", jsonNode).textValue();
    }

    public static boolean isEntityCreation(final JsonNode jsonNode) {
        return Objects.equals(getNodeAtIn("/op", jsonNode).asText(), DebeziumOperation.CREATE.getValue());
    }

    public static boolean isEntityUpdate(final JsonNode jsonNode) {
        return Objects.equals(getNodeAtIn("/op", jsonNode).asText(), DebeziumOperation.UPDATE.getValue());
    }

    public static boolean isEntityDeletion(final JsonNode jsonNode) {
        return Objects.equals(getNodeAtIn("/op", jsonNode).asText(), DebeziumOperation.DELETE.getValue());
    }

    public static void logKafkaMessage(
            final Logger logger, final String topic, final ConsumerRecord<String, JsonNode> msg, final Long offset) {
        if (logger.isDebugEnabled()) {
            logger.debug("Kafka message arrived: topic: {}, key: {}, value: {}, offset: {}",
                    topic, msg.key(), msg.value(), offset);
        }
    }

    public static void logKafkaMessage(
            final Logger logger, final String topic, final ConsumerRecord<String, JsonNode> msg) {
        if (logger.isDebugEnabled()) {
            logger.debug("Kafka message arrived: topic: {}, key: {}, value: {}", topic, msg.key(), msg.value());
        }
    }

    public static JsonNode getNodeAtIn(final String jsonPtrExpr, final JsonNode jsonNode) {
        if (jsonNode == null) {
            final var msg = String.format("Kafka message with empty value arrived, cannot extract '%s'", jsonPtrExpr);
            throw new IllegalArgumentException(msg);
        }

        if (!jsonNode.at(PAYLOAD_PATH + jsonPtrExpr).isMissingNode()) {
            return jsonNode.at(PAYLOAD_PATH + jsonPtrExpr);
        }
        else if (!jsonNode.at(jsonPtrExpr).isMissingNode()) {
            return jsonNode.at(jsonPtrExpr);
        }
        else {
            throw new IllegalArgumentException(String.format("Cannot find \"%s\" in the arrived object", jsonPtrExpr));
        }
    }

}
