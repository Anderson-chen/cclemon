package org.cclemon.dispatcher;

public interface KafkaListenerDispatcher {
    void dispatch(String topic, String key, String message);
}
