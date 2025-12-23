package org.cclemon.comsumer;

import lombok.AllArgsConstructor;
import org.cclemon.handler.KafkaEventHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaConsumer {
    
    private final KafkaEventHandler kafkaEventHandler;

    @KafkaListener(topics = "${kafka.topic}", groupId = "my-consumer-group-1")
    public void listen(String message) {
        kafkaEventHandler.handle(message);
    }
}
