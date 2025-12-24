package org.cclemon.producer;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final String topic;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, @Value("${kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void sendMessage(String messageContent) {
        Message<String> message = MessageBuilder
                .withPayload(messageContent)
                .setHeader("key", "myKey") // 可選 header
                .build();

        kafkaTemplate.send(topic, UUID.randomUUID().toString(), message.getPayload()); // 將 payload 送出
    }

}
