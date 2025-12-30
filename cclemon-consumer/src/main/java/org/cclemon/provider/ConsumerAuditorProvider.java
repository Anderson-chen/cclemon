package org.cclemon.provider;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ConsumerAuditorProvider implements AuditorProvider {
    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.of(-2L); // 預設的 userId，表示來自 Consumer
    }
}
