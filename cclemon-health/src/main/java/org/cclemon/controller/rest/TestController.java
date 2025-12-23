package org.cclemon.controller.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.Exercise;
import org.cclemon.logging.annotation.LogExecutionTime;
import org.cclemon.producer.KafkaProducerService;
import org.cclemon.service.ExerciseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class TestController {
    private final ExerciseService exerciseService;
    private final KafkaProducerService kafkaProducerService;

    @GetMapping(value = "/test")
    @LogExecutionTime
    public Exercise test() throws InterruptedException {
        kafkaProducerService.sendMessage("TEST1");
        return exerciseService.insert();
    }

//    @GetMapping("/test")
//    public CompletableFuture<String> test() {
//        System.out.println(Thread.currentThread());
//        return CompletableFuture.supplyAsync(() -> "done",
//                CompletableFuture.delayedExecutor(3000, TimeUnit.MILLISECONDS));
//    }
}
