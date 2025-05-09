package org.cclemon.config;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private String topic;

    @KafkaListener(topics = "${kafka.topic}", groupId = "my-consumer-group")
    public void listen(String message) {
        System.out.println("Received Kafka message: " + message);
    }

}
