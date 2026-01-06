package org.cclemon.repository;

import org.cclemon.adapter.DelegatingAuditorAware;
import org.cclemon.config.JpaAuditingConfig;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserWeightLog;
import org.cclemon.entity.WeightSource;
import org.cclemon.provider.AuditorProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({DelegatingAuditorAware.class, JpaAuditingConfig.class})
class UserWeightLogRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserWeightLogRepository userWeightLogRepository;

    private CclemonUser user1;
    private CclemonUser user2;
    private UserWeightLog log1;

    // 定義一個測試用的 AuditorProvider，固定返回 ID 999
    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuditorProvider auditorProvider() {
            return () -> Optional.of(999L);
        }
    }

    @BeforeEach
    void setUp() {
        // Create and persist User 1
        user1 = new CclemonUser();
        user1.setUsername("user1");
        entityManager.persistAndFlush(user1);

        // Create and persist User 2
        user2 = new CclemonUser();
        user2.setUsername("user2");
        entityManager.persistAndFlush(user2);

        // Persist a record for user1
        log1 = new UserWeightLog();
        log1.setUser(user1);
        log1.setMeasureDate(LocalDate.now());
        log1.setWeightKg(new BigDecimal("75.5"));
        log1.setSource(WeightSource.MANUAL);
        log1.setDeleted(false);
        entityManager.persistAndFlush(log1);

        // Persist a record for user2
        UserWeightLog log2 = new UserWeightLog();
        log2.setUser(user2);
        log2.setMeasureDate(LocalDate.now());
        log2.setWeightKg(new BigDecimal("65.0"));
        log2.setSource(WeightSource.MANUAL);
        log2.setDeleted(false);
        entityManager.persistAndFlush(log2);
    }

    @Test
    void findByUser_IdAndMeasureDateAndSourceAndDeletedFalse_whenRecordExists_shouldReturnRecord() {
        Optional<UserWeightLog> found = userWeightLogRepository.findByUser_IdAndMeasureDateAndSourceAndDeletedFalse(
                user1.getId(), LocalDate.now(), WeightSource.MANUAL);
        assertThat(found).isPresent();
        assertThat(found.get().getWeightKg()).isEqualByComparingTo("75.5");

        // 驗證 Auditing 是否生效
        assertThat(found.get().getCreateTime()).isNotNull();
        assertThat(found.get().getCreateUserId()).isEqualTo(999L);
    }

    @Test
    void findByUser_IdAndMeasureDateBetweenAndDeletedFalseOrderByMeasureDateDesc_shouldReturnMatchingRecords() {
        List<UserWeightLog> results = userWeightLogRepository.findByUser_IdAndMeasureDateBetweenAndDeletedFalseOrderByMeasureDateDesc(
                user1.getId(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUser().getId()).isEqualTo(user1.getId());
    }

    @Test
    void existsByUser_IdAndMeasureDateAndDeletedFalse_whenRecordExists_shouldReturnTrue() {
        boolean exists = userWeightLogRepository.existsByUser_IdAndMeasureDateAndDeletedFalse(user1.getId(), LocalDate.now());
        assertThat(exists).isTrue();
    }

    @Test
    void existsByUser_IdAndMeasureDateAndDeletedFalse_whenRecordDoesNotExist_shouldReturnFalse() {
        boolean exists = userWeightLogRepository.existsByUser_IdAndMeasureDateAndDeletedFalse(user1.getId(), LocalDate.now().minusDays(1));
        assertThat(exists).isFalse();
    }

    @Test
    void findByIdAndUser_Id_whenCorrectUser_shouldReturnRecord() {
        Optional<UserWeightLog> found = userWeightLogRepository.findByIdAndUser_Id(log1.getId(), user1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(log1.getId());
    }

    @Test
    void findByIdAndUser_Id_whenIncorrectUser_shouldReturnEmpty() {
        Optional<UserWeightLog> found = userWeightLogRepository.findByIdAndUser_Id(log1.getId(), user2.getId());
        assertThat(found).isNotPresent();
    }
}
