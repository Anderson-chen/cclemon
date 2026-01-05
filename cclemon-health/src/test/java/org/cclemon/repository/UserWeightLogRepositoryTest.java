package org.cclemon.repository;


import lombok.RequiredArgsConstructor;
import org.cclemon.adapter.DelegatingAuditorAware;
import org.cclemon.config.JpaAuditingConfig;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserWeightLog;
import org.cclemon.provider.SpringSecurityAuditorProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({DelegatingAuditorAware.class, JpaAuditingConfig.class, SpringSecurityAuditorProvider.class})
class UserWeightLogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserWeightLogRepository userWeightLogRepository;

    @Test
    void whenSave_thenFindById_shouldReturnLog() {
        // Arrange: 準備測試資料
        CclemonUser user = new CclemonUser();
        user.setUsername("test-user");
        entityManager.persist(user);

        UserWeightLog newLog = new UserWeightLog();
        newLog.setUser(user);
        newLog.setMeasureDate(LocalDate.now());
        newLog.setWeightKg(BigDecimal.valueOf(75.5));

        // Act: 執行被測試的邏輯
        UserWeightLog savedLog = userWeightLogRepository.save(newLog);
        Optional<UserWeightLog> foundLogOptional = userWeightLogRepository.findById(savedLog.getId());

        // Assert: 驗證結果
        assertThat(foundLogOptional).isPresent();
        UserWeightLog foundLog = foundLogOptional.get();

        // 驗證業務欄位
        assertThat(foundLog.getWeightKg()).isEqualByComparingTo("75.5");
        assertThat(foundLog.getUser().getUsername()).isEqualTo("test-user");

        // 驗證審計欄位 (Auditing)
        assertThat(foundLog.getCreateTime()).isNotNull();
        assertThat(foundLog.getLastModifiedTime()).isNotNull();
        assertThat(foundLog.getCreateUserId()).isEqualTo(-1L); // 根據 JpaConfig 中的設定
        assertThat(foundLog.getLastModifiedUserId()).isEqualTo(-1L); // 根據 JpaConfig 中的設定
    }
}
