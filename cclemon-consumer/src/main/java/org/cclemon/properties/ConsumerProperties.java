package org.cclemon.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cclemon")
@Data
public class ConsumerProperties {

    private List<ConsumerConfig> consumers;

    public record ConsumerConfig(String topic, String groupId) {
    }
}
