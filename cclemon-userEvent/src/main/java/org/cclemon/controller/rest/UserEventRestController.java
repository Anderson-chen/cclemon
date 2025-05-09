package org.cclemon.controller.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import org.cclemon.service.KafkaProducerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@AllArgsConstructor
@Log
public class UserEventRestController {

    // 假設我們有一個 Kafka producer 來發送事件到 Kafka
    private final KafkaProducerService kafkaProducerService;

    // API 用來接收用戶點擊事件
    @PostMapping("/user-events")
    public void recordClickEvent(@RequestBody String clickEvent) {
        kafkaProducerService.sendMessage(clickEvent);
    }

//    @Data
//    public static class UserClickEvent {
//        // Getters and Setters
//        private String userId;
//        private String elementId;  // 被點擊的元素的 ID
//        private LocalDateTime timestamp;
//    }
}
