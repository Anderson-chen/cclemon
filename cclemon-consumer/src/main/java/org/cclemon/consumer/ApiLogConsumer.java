package org.cclemon.consumer;

import org.cclemon.comsumer.AbstractKafkaConsumer;
import org.cclemon.entity.system.ApiLog;
import org.cclemon.repository.system.ApiLogRepository;
import org.springframework.stereotype.Component;

@Component
public class ApiLogConsumer extends AbstractKafkaConsumer<ApiLog> {

    private final ApiLogRepository apiLogRepository;

    public ApiLogConsumer(ApiLogRepository apiLogRepository) {
        super(ApiLog.class);
        this.apiLogRepository = apiLogRepository;
    }

    @Override
    protected void handle(ApiLog apiLog) {
        apiLogRepository.save(apiLog);
    }
}
