package org.cclemon.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {
    private String topic;

    @KafkaListener(topics = "${kafka.topic}", groupId = "my-consumer-group-1")
    public void listen1_1(String message) {
        log.info("my-consumer-group-1-1 Received Kafka message: {}", message);
    }

    @KafkaListener(topics = "${kafka.topic}", groupId = "my-consumer-group-1")
    public void listen1_2(String message) {
        log.info("my-consumer-group-1-2 Received Kafka message: {}", message);
    }

//    @KafkaListener(topics = "${kafka.topic}", groupId = "my-consumer-group-2")
    public void listen2(String message) {
        log.info("my-consumer-group-2 Received Kafka message: {}", message);
    }
}
