package org.cclemon.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            // 檢查當前是否有登入使用者，若沒有則返回預設值
            if (SecurityContextHolder.getContext().getAuthentication() == null ||  StringUtils.equals(SecurityContextHolder.getContext().getAuthentication().getName(), "anonymousUser")) {
                return Optional.of(-1L); // 預設的 userId
            }
            // 否則返回目前登入的使用者 ID
            return Optional.of(Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()));
        };
    }
}
