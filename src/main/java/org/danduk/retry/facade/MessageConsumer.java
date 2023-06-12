package org.danduk.retry.facade;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.danduk.retry.domain.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;

import java.lang.invoke.MethodHandles;


public class MessageConsumer {
     ProcessorService processorService;

     static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


     public MessageConsumer(ProcessorService processorService) {
          this.processorService = processorService;
     }

     @RetryableTopic(attempts = "3",
                backoff = @Backoff(delay = 700, maxDelay = 12000, multiplier = 3))
     @KafkaListener(topics = "products.public.product")
     public void processMessage(ConsumerRecord message) {
          try {
               this.processorService.processMessage(message);
          }
          catch (Exception e){
               logger.error("Got an error in processing: {}", e.toString());
          }
     }

}
