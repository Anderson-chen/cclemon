package org.cclemon.handler;

/**
 * 處理特定 Kafka Topic 訊息的介面
 */
public interface KafkaMessageHandler {

    /**
     * 回傳此 Handler 負責的 Topic 名稱
     */
    String getTopic();

    void handle(String key, String message);
}