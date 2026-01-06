package org.cclemon;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
// 修正 Import：使用 Spring Boot 4.x 的正確路徑
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.cclemon.api.WeightHandler;

@SpringBootTest
class CclemonJobApplicationTest {

    // Mock any handlers that the job module might depend on
    @MockitoBean
    private WeightHandler weightHandler;

    @Test
    void contextLoads() {
        // This test will pass if the application context can be loaded successfully.
    }

}
