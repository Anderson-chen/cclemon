package org.cclemon.repository;

import jakarta.transaction.Transactional;
import org.cclemon.entity.CclemonUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;


@SpringBootTest
class CclemonUserRepositoryTest {

    @Autowired
    CclemonUserRepository cclemonUserRepository;

    @Test
    void testSave() {
        var user = new CclemonUser();
        user.setUsername("Andy");
        user.setTargetCalorie(2000L);
        user.setTargetWeight(75L);
        cclemonUserRepository.save(user);

    }

    @Test
    void testUpdate() {
        var user = cclemonUserRepository.findById(10L).get();
        user.setUsername("Andy12");
        cclemonUserRepository.save(user);
    }

    @Test
    void testConcurrentUpdateWithVirtualThreads() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Runnable task = () -> {
            try {
                // 等待另一個執行緒準備好
//                latch.await();
                // 獨立的 EntityManager 讀取
                CclemonUser user = cclemonUserRepository.findById(10L).orElseThrow();
                var name = UUID.randomUUID().toString();
                System.out.println(name);
                user.setUsername(name);
                Thread.sleep(100); // 模擬處理延遲，增加衝突可能性
                cclemonUserRepository.save(user);
                System.out.println("Updated by: " + Thread.currentThread().getName());
            } catch (Exception e) {
                System.err.println("Error in " + Thread.currentThread().getName() + ": " + e.getMessage());
            }
        };

        // 啟動兩個虛擬執行緒
//        Thread t1 = new Thread(task);
//        Thread t2 = new Thread(task);
//        t1.start();  // <== 這兩行是必要的
//        t2.start();

        // 設定執行緒的名稱格式
        Thread.Builder builder = Thread.ofVirtual().name("My-VThread-", 0);
        Thread t1 = builder.start(task);
//        Thread.sleep(2000);
        Thread t2 = builder.start(task);


        // 同時開始
//        latch.countDown();

        // 等待執行緒完成
        t1.join();
        t2.join();
    }

}