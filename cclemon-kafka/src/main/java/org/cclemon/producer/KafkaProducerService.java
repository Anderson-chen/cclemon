package org.cclemon.producer;


import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

@Service
@AllArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

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
