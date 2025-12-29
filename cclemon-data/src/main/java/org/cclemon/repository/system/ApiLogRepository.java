package org.cclemon.repository.system;

import org.cclemon.entity.system.ApiLog;
import org.cclemon.entity.system.ConfigCacheVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {
}
