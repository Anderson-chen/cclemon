package org.cclemon.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaEventHandlerImpl implements KafkaEventHandler {


    @Override
    public void handle(String message) {
        log.info(message);
    }
}
