package org.cclemon.handler;

import lombok.AllArgsConstructor;
import org.cclemon.entity.system.ApiLog;
import org.cclemon.repository.system.ApiLogRepository;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@AllArgsConstructor
public class ApiLogHandlerImpl implements KafkaMessageHandler {

    private ApiLogRepository apiLogRepository;
    private JsonMapper jsonMapper;

    @Override
    public String getTopic() {
        return "CCLEMON_HEALTH_API_LOG";
    }

    @Override
    public void handle(String key, String message) {
        apiLogRepository.save(jsonMapper.readValue(message, ApiLog.class));
        apiLogRepository.flush();
    }
}
