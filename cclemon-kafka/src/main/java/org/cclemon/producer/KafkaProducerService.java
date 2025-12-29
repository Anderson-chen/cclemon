package org.cclemon.producer;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private JsonMapper jsonMapper;
    private final String topic;


    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, JsonMapper jsonMapper, @Value("${kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
        this.topic = topic;
    }

    public void sendMessage(String messageContent) {
        Message<String> message = MessageBuilder
                .withPayload(messageContent)
                .setHeader("key", "myKey") // 可選 header
                .build();

        kafkaTemplate.send(topic, UUID.randomUUID().toString(), message.getPayload()); // 將 payload 送出
    }

    public <T> void sendMessage(T messageContent, String topic) {

        String payload = switch (messageContent) {
            case String s -> s;  // 如果是 String，直接使用
            case null -> throw new IllegalArgumentException("Message content cannot be null");
            default -> jsonMapper.writeValueAsString(messageContent); // 其他類型序列化成 JSON
        };

        Message<String> message = MessageBuilder
                .withPayload(payload)
                .setHeader("key", "myKey") // 可選 header
                .build();

        kafkaTemplate.send(topic, UUID.randomUUID().toString(), message.getPayload()); // 將 payload 送出
    }

}
