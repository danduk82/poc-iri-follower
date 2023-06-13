package org.danduk.retry.domain.serdes;

import com.fasterxml.jackson.databind.JsonNode;

public class KafkaNotificationUtil {

    public KafkaNotificationUtil() {

    }

    public static JsonNode getNodeByName(final JsonNode jsonNode, final String nodeName) {
        final JsonNode node;
        if (jsonNode.at("/payload/after/" + nodeName).isValueNode()) {
            node = jsonNode.at("/payload/after/" + nodeName);
        }
        else if (jsonNode.at("/after/" + nodeName).isValueNode()) {
            node = jsonNode.at("/after/" + nodeName);
        }
        else {
            final var msg = String.format("Cannot find %s in the arrived object", nodeName);
            throw new IllegalArgumentException(msg);
        }
        return node;
    }
}