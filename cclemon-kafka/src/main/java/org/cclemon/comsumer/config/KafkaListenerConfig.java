package org.cclemon.comsumer.config;

import org.cclemon.comsumer.PrototypeKafkaListener;
import org.cclemon.dispatcher.KafkaListenerDispatcher;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class KafkaListenerConfig {
    // Bean 定義為 prototype，每次呼叫 getBean() 都會產生新的 listener
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    PrototypeKafkaListener prototypeKafkaListenerBean(String id, String topic, String groupId, KafkaListenerDispatcher kafkaListenerDispatcher) {
        return new PrototypeKafkaListener(id, topic, groupId, kafkaListenerDispatcher);
    }
}
