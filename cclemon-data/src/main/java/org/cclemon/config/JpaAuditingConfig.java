package org.cclemon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "delegatingAuditorAware")
@Configuration
public class JpaAuditingConfig {
}
