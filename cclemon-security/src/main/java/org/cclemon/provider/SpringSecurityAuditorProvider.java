package org.cclemon.provider;

import org.apache.commons.lang3.StringUtils;
import org.cclemon.AuditorProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorProvider implements AuditorProvider {
    @Override
    public Optional<Long> getCurrentAuditor() {
        // 檢查當前是否有登入使用者，若沒有則返回預設值
        if (SecurityContextHolder.getContext().getAuthentication() == null || StringUtils.equals(SecurityContextHolder.getContext().getAuthentication().getName(), "anonymousUser")) {
            return Optional.of(-1L); // 預設的 userId
        }
        // 否則返回目前登入的使用者 ID
        return Optional.of(Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()));
    }
}
