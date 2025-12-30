package org.cclemon.dispatcher;


import lombok.extern.slf4j.Slf4j;
import org.cclemon.handler.KafkaMessageHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KafkaListenerDispatcherImpl implements KafkaListenerDispatcher {

    private final Map<String, KafkaMessageHandler> handlerMap;

    // Spring 會自動注入所有實作 KafkaMessageHandler 的 Bean
    public KafkaListenerDispatcherImpl(List<KafkaMessageHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(KafkaMessageHandler::getTopic, Function.identity()));
    }

    @Override
    public void dispatch(String topic, String key, String message) {
        log.debug("Dispatching message for topic: {}, key: {}", topic, key);

        org.cclemon.handler.KafkaMessageHandler handler = handlerMap.get(topic);
        if (handler == null) {
            log.warn("No handler found for topic: {}", topic);
            return;
        }

        try {
            handler.handle(key, message);
        } catch (Exception e) {
            log.error("Error processing message for topic: {}, key: {}", topic, key, e);
            // 根據需求，這裡可以選擇 throw e 來觸發 Kafka 的重試機制 (Retry/DLQ)
        }
    }
}
