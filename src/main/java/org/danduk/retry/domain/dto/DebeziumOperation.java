package org.danduk.retry.domain.dto;

public enum DebeziumOperation {
    CREATE("c"),
    READ("r"),

    UPDATE("u"),

    DELETE("d");

    private final String value;

    DebeziumOperation(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean isAnyOf(final DebeziumOperation... debeziumOperations) {
        for (final DebeziumOperation debeziumOperation : debeziumOperations) {
            if (this == debeziumOperation) {
                return true;
            }
        }
        return false;
    }
}
