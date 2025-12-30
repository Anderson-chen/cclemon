package org.cclemon.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.cclemon.comsumer.PrototypeKafkaListener;
import org.cclemon.dispatcher.KafkaListenerDispatcher;
import org.cclemon.properties.ConsumerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsumerService {

    @PostConstruct
    public void init() {
        Optional.ofNullable(consumerProperties.getConsumers())
                .ifPresent(configs -> configs.forEach(this::createConsumer));
    }

    private final ApplicationContext context;
    private final KafkaListenerDispatcher kafkaListenerDispatcher;
    private final ConsumerProperties consumerProperties;


    public void createConsumer(ConsumerProperties.ConsumerConfig consumerConfig) {
        context.getBean(PrototypeKafkaListener.class, UUID.randomUUID().toString(), consumerConfig.topic(), consumerConfig.groupId(), kafkaListenerDispatcher);
    }
}
