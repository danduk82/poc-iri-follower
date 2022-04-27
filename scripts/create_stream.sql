
SET 'auto.offset.reset' = 'earliest';
CREATE STREAM iri_events (
    iri_id BIGINT,
    timestamp VARCHAR
) WITH (
    kafka_topic = 'iri_events',
    partitions = 8,
    value_format = 'avro',
    timestamp = 'timestamp',
    timestamp_format = 'yyyy-MM-dd''T''HH:mm:ssZ',
    VALUE_AVRO_SCHEMA_FULL_NAME = 'iri.events.IriEvents'
);
