package org.cclemon.controller.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.Exercise;
import org.cclemon.service.ExerciseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class TestController {

    ExerciseService exerciseService;

    @GetMapping(value = "/test")
    public Exercise test() throws InterruptedException {
        System.out.println(Thread.currentThread());
        Redisson
        return exerciseService.insert();
    }

//    @GetMapping("/test")
//    public CompletableFuture<String> test() {
//        System.out.println(Thread.currentThread());
//        return CompletableFuture.supplyAsync(() -> "done",
//                CompletableFuture.delayedExecutor(3000, TimeUnit.MILLISECONDS));
//    }
}
