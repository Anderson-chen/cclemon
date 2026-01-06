package org.cclemon.health.internal.service;

import lombok.RequiredArgsConstructor;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserWeightLog;
import org.cclemon.entity.WeightSource;
import org.cclemon.repository.CclemonUserRepository;
import org.cclemon.repository.UserWeightLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WeightService {

    private final UserWeightLogRepository userWeightLogRepository;
    private final CclemonUserRepository cclemonUserRepository;

    @Transactional
    public UserWeightLog upsertManualWeight(Long userId,
                                            LocalDate measureDate,
                                            BigDecimal weightKg,
                                            LocalTime measureTime,
                                            String note) {

        // 先查詢 User，確保使用者存在
        CclemonUser user = cclemonUserRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        UserWeightLog log = userWeightLogRepository
                .findByUser_IdAndMeasureDateAndSourceAndDeletedFalse(userId, measureDate, WeightSource.MANUAL)
                .orElseGet(UserWeightLog::new);

        log.setUser(user); // 設定關聯物件
        log.setMeasureDate(measureDate);
        log.setMeasureTime(measureTime);
        log.setWeightKg(weightKg);
        log.setNote(note);
        log.setSource(WeightSource.MANUAL);

        return userWeightLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<UserWeightLog> listWeights(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return userWeightLogRepository
                .findByUser_IdAndMeasureDateBetweenAndDeletedFalseOrderByMeasureDateDesc(userId, startDate, endDate, pageable);
    }

    @Transactional(readOnly = true)
    public List<UserWeightLog> listWeights(Long userId, LocalDate startDate, LocalDate endDate) {
        return userWeightLogRepository
                .findByUser_IdAndMeasureDateBetweenAndDeletedFalseOrderByMeasureDateDesc(userId, startDate, endDate);
    }

    @Transactional
    public void softDelete(Long userId, Long logId) {
        UserWeightLog log = userWeightLogRepository.findByIdAndUser_Id(logId, userId)
                .orElseThrow(() -> new NoSuchElementException("Weight log not found with id: " + logId + " for user: " + userId));
        log.setDeleted(true);
        userWeightLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public boolean hasRecord(Long userId, LocalDate date) {
        return userWeightLogRepository.existsByUser_IdAndMeasureDateAndDeletedFalse(userId, date);
    }
}
