package org.cclemon.handler;

public interface KafkaEventHandler {
    public void handle(String message);
}
