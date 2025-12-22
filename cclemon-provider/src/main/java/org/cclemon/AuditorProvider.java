package org.cclemon;

import java.util.Optional;

public interface AuditorProvider {
    Optional<Long> getCurrentAuditor();
}
