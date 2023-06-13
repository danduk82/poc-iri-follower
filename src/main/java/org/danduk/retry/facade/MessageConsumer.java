package org.danduk.retry.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import com.fasterxml.jackson.databind.JsonNode;
import org.danduk.retry.GisServiceException;
import org.danduk.retry.domain.dto.Product;
import org.danduk.retry.domain.serdes.KafkaNotificationUtil;
import org.danduk.retry.integration.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.FixedDelayStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class MessageConsumer {

     @Autowired

     private final ProductService productService;
     private final KafkaNotificationUtil kafkaNotificationUtil;
     private final ObjectMapper mapper = new ObjectMapper();
     static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


     public MessageConsumer(ProductService productService, KafkaNotificationUtil kafkaNotificationUtil) {
          this.productService = productService;
          this.kafkaNotificationUtil = kafkaNotificationUtil;
     }

     @RetryableTopic(
             attempts = "#{'${poc.retry.maxRetryAttempts}'}",
             autoCreateTopics = "#{'${poc.retry.autoCreateRetryTopics}'}",
             backoff = @Backoff(delayExpression = "#{'${poc.retry.retryIntervalMilliseconds}'}", multiplierExpression = "#{'${poc.retry.retryBackoffMultiplier}'}"),
             fixedDelayTopicStrategy = FixedDelayStrategy.MULTIPLE_TOPICS,
             timeout = "#{'${poc.retry.maxRetryDurationMilliseconds}'}",
             topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE)

     @KafkaListener(topics = "${poc.topics.topic-name}")
     public void processMessage(ConsumerRecord<String, String> record,
                                Acknowledgment ack,
                                @Header(KafkaHeaders.OFFSET) final Long offset) throws GisServiceException {
          try {
               Long productId = kafkaNotificationUtil.getNodeByName(this.mapper.readTree(record.value()),"iri_id").asLong();
               Product product = this.productService.getProductById(productId);
               logger.info(product.toString());
          }
          catch (GisServiceException e) {
               throw e;
          }
          catch (Exception e){
               System.out.println(record.toString());
               logger.error("Got an error in processing: {}", e.toString());
          }
          finally {
               ack.acknowledge();
          }
     }

}
