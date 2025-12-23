package org.cclemon.adapter;

import org.cclemon.provider.AuditorProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("delegatingAuditorAware")
public class DelegatingAuditorAware implements AuditorAware<Long> {

    private final AuditorProvider auditorProvider;

    public DelegatingAuditorAware(AuditorProvider auditorProvider) {
        this.auditorProvider = auditorProvider;
    }

    @Override
    public Optional<Long> getCurrentAuditor() {
        return auditorProvider.getCurrentAuditor();
    }
}

