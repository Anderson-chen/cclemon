package org.cclemon.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


@SpringBootTest
class KafkaProducerServiceTest {

    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Test
    public void testSendMessage() {

        ExecutorService executors = Executors.newFixedThreadPool(10);

        CompletableFuture.supplyAsync(() -> "123", executors);
    }

    public void send() {
        kafkaProducerService.sendMessage(LocalDateTime.now().format(dateTimeFormatter));
    }

    public static void test() throws InterruptedException {
        Thread.sleep(2000);
    }

    public static void main(String[] args) {

        long start = System.currentTimeMillis();

        ExecutorService executors = Executors.newFixedThreadPool(5);

        List<CompletableFuture<Void>> futures =
                IntStream.rangeClosed(0, 100)
                        .mapToObj(i ->
                                CompletableFuture.runAsync(() -> {
                                    try {
                                        System.out.println("Task " + i + " on " +
                                                Thread.currentThread().getName());
                                        test();
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                }, executors)
                        ).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long end = System.currentTimeMillis();


        System.out.println("DONE");
        System.out.println("Total: " + (end - start));
    }
}