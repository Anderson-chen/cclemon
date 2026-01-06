package org.cclemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CclemonJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(CclemonJobApplication.class, args);
    }

}
