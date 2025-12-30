package org.cclemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("org.cclemon")
public class CclemonConsumerApplication {
    static void main(String[] args) {
        SpringApplication.run(CclemonConsumerApplication.class, args);
    }
}
