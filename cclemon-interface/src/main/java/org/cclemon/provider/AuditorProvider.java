package org.cclemon.provider;

import java.util.Optional;

public interface AuditorProvider {
    Optional<Long> getCurrentAuditor();
}
