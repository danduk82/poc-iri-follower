package iri.events;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class IriSubscriber {

    // Matches the broker port specified in the Docker Compose file.
    private final static String BOOTSTRAP_SERVERS = "localhost:29092";
    // Matches the Schema Registry port specified in the Docker Compose file.
    private final static String SCHEMA_REGISTRY_URL = "http://localhost:8081";
    // Matches the topic name specified in the ksqlDB CREATE TABLE statement.
    private final static String TOPIC = "products.public.product";

    private final static DbConnector dbConnector = new DbConnector("jdbc:postgresql://localhost:61543/postgres", "postgres", "postgres");

    public DbConnector getDbConnector() {
        return dbConnector;
    }

    private final static DateTimeFormatter formatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.US)
                    .withZone(ZoneId.systemDefault());

    public static void main(final String[] args) throws IOException {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "iri-follower");
        // we want transactions to happen, se we disable auto-commit
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        //props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        //props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

        try (final KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(TOPIC));

            // connect to geoserver
//            GeoserverAPI geoserver = new GeoserverAPI("http://localhost:61590/geoserver/ows?service=wfs&version=1.1.0&request=GetCapabilities");
//            for (String name: geoserver.getDatastore().getTypeNames()) {
//                System.out.println(name);
//            }

            // TODO: scheduled task
            // run every xx time -> publish event
            // service (spring comp. @service) -> listener of these events
            // spring cloud bus to use it should extend "RemoteApplicationEvent"
            boolean commitOk = true;
            try {
                System.out.println("ready");
                while (true) {
                    ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(1000));
                    for (TopicPartition partition : records.partitions()) {
                        System.out.println(String.format("partition: %s",partition.toString()));
                        List<ConsumerRecord<String, GenericRecord>> partitionRecords = records.records(partition);
                        commitOk = true;
                        for (ConsumerRecord<String, GenericRecord> record : partitionRecords) {
                            final GenericRecord value;
                            try {
                                value = (GenericRecord) record.value().get("after");
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                continue;
                            }
                            if (value != null) {
                                if (reactToEvent(value)){
                                    commitOk = false;
                                }
                            }
                        }
                        long lastConsumedOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                        if (commitOk) {
                            consumer.commitSync(Collections.singletonMap(partition,
                                    new OffsetAndMetadata(lastConsumedOffset + 1)));
                        }
                    }
                }
            } finally {
                consumer.close();
            }

        }
    }

    private static boolean reactToEvent(GenericRecord iri_event) throws IOException {

        String content = makeContent(iri_event);
        System.out.println(content);
        //ProductWrapper product = ProductWrapper(iri_event.getIRIID());

        ProductWrapper productWrapper = null;
        try {
            productWrapper = new ProductWrapper((Long) iri_event.get("iri_id"));
            System.out.println(productWrapper.getProduct().toString());
            System.out.println(productWrapper.getProduct().getIritimestamp());
            dbConnector.insertProduct(productWrapper.getProduct());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    private static String makeContent(GenericRecord iri_event) {
        return String.format("Received new IRI, with IRI_ID = %s created at time: %s",
                iri_event.get("iri_id"),
                iri_event.get("iritimestamp"));
    }

}

