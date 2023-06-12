package org.danduk.retry.facade;

import org.codehaus.jackson.map.ObjectMapper;
import org.danduk.retry.domain.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;


public class MessageConsumer {
     ProcessorService processorService;

     public MessageConsumer(ProcessorService processorService) {
          this.processorService = processorService;
     }

     @RetryableTopic(attempts = 3,
                backoff = @Backoff(delay = 700, maxDelay = 12000, multiplier = 3))
     @KafkaListener(topics = "my-annotated-topic")
     public void processMessage(String message) {
          int productId = ObjectMapper
          this.processorService.processMessage();
     }

}
