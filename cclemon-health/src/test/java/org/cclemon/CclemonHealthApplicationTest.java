package org.cclemon;

import org.cclemon.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CclemonHealthApplicationTest {

    @Autowired
    RedisService redisService;

    @Test
    void testRedis() {
        redisService.set("test", "hey1");
        System.out.println(redisService.get("test"));
    }


}