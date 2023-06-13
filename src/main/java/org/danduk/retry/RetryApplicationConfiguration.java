package org.danduk.retry;


import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import lombok.extern.slf4j.Slf4j;
import org.danduk.retry.domain.serdes.KafkaNotificationUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.schema.registry.client.ConfluentSchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RetryApplicationConfiguration {
    @Bean
    public ConfluentSchemaRegistryClient schemaRegistryClient(@Value("${spring.cloud.schemaRegistryClient.endpoint}") String endpoint){
        ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
        client.setEndpoint(endpoint);
        return client;
    }

    @Bean
    public KafkaNotificationUtil kafkaNotificationUtil(){
        KafkaNotificationUtil kafkaNotificationUtil = new KafkaNotificationUtil();
        return kafkaNotificationUtil;
    }
}
