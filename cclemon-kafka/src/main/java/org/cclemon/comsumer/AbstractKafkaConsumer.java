package org.cclemon.comsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import tools.jackson.databind.json.JsonMapper;

public abstract class AbstractKafkaConsumer<T> {

    @Autowired
    private JsonMapper jsonMapper; // 用於序列化/反序列化

    private final Class<T> type;

    protected AbstractKafkaConsumer(Class<T> type) {
        this.type = type;
    }

    protected abstract void handle(T message);

    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.group}")
    public void listen(String rawMessage) {
        T message = preProcess(rawMessage);
        handle(message);
        postProcess(message);
    }

    protected T preProcess(String rawMessage) {
        return jsonMapper.readValue(rawMessage, type);
    }

    protected void postProcess(T message) {
    }
}
