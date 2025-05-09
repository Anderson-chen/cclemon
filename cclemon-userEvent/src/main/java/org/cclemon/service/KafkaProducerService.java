package org.cclemon.service;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${kafka.topic}")
    private String topic;

    public void sendMessage(String messageContent) {
        Message<String> message = MessageBuilder
                .withPayload(messageContent)
                .setHeader("key", "myKey") // 可選 header
                .build();

        kafkaTemplate.send(topic, message.getPayload()); // 將 payload 送出
    }

}
