package org.cclemon.comsumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.dispatcher.KafkaListenerDispatcher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@Data
@AllArgsConstructor
@Slf4j
public class PrototypeKafkaListener {

    private final String id;
    private final String topic;
    private final String groupId;
    private final KafkaListenerDispatcher kafkaDispatcher;

    // KafkaListener 動態綁定 id 和 topic
    @KafkaListener(id = "#{__listener.id}", topics = "#{__listener.topic}",groupId = "#{__listener.groupId}")
    public void listen(@Payload String message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Listener[{}] received from topic[{}]: message : {} , key : {}", id, topic, message, key);
        kafkaDispatcher.dispatch(topic, key, message);
    }
}