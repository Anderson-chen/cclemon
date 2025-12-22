package org.cclemon.service;

import lombok.AllArgsConstructor;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.UserWeightLog;
import org.cclemon.entity.WeightSource;
import org.cclemon.repository.CclemonUserRepository;
import org.cclemon.repository.UserWeightLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
@AllArgsConstructor
public class WeightService {

    private final UserWeightLogRepository userWeightLogRepository;
    private final CclemonUserRepository cclemonUserRepository;

    // TODO: 之後可考慮抽成共用的 Auth 工具類別。
    private CclemonUser getCurrentUser() {

        return null;
    }

    /**
     * 根據使用者與測量日期建立或更新一筆手動輸入的體重紀錄。
     * <p>
     * 若同一 user + date + source(MANUAL) 已存在紀錄，會更新該筆資料；否則新增一筆。
     */
    @Transactional
    public UserWeightLog upsertManualWeight(LocalDate measureDate,
                                            BigDecimal weightKg,
                                            LocalTime measureTime,
                                            String note) {
        CclemonUser user = getCurrentUser();

        UserWeightLog log = userWeightLogRepository
                .findByUserAndMeasureDateAndSourceAndDeletedFalse(user, measureDate, WeightSource.MANUAL)
                .orElseGet(UserWeightLog::new);

        log.setUser(user);
        log.setMeasureDate(measureDate);
        log.setMeasureTime(measureTime);
        log.setWeightKg(weightKg);
        log.setNote(note);
        log.setSource(WeightSource.MANUAL);

        return userWeightLogRepository.save(log);
    }

    /**
     * 取得指定日期區間內的體重紀錄清單，並附帶與前一筆紀錄的差異值（diffFromPrev）。
     */
    @Transactional(readOnly = true)
    public List<WeightListItem> listWeights(LocalDate startDate, LocalDate endDate) {
        CclemonUser user = getCurrentUser();
        List<UserWeightLog> logs = userWeightLogRepository
                .findByUserAndMeasureDateBetweenAndDeletedFalseOrderByMeasureDateDescMeasureTimeDesc(user, startDate, endDate);

        // compute diffFromPrev based on chronological order
        List<UserWeightLog> chronological = new ArrayList<>(logs);
        chronological.sort(Comparator.comparing(UserWeightLog::getMeasureDate)
                .thenComparing(UserWeightLog::getMeasureTime, Comparator.nullsLast(Comparator.naturalOrder())));

        Double previousWeight = null;
        Map<Long, Double> diffMap = new HashMap<>();
        for (UserWeightLog log : chronological) {
            double current = log.getWeightKg().doubleValue();
            if (previousWeight == null) {
                diffMap.put(log.getId(), null);
            } else {
                diffMap.put(log.getId(), current - previousWeight);
            }
            previousWeight = current;
        }

        List<WeightListItem> result = new ArrayList<>();
        for (UserWeightLog log : logs) { // logs already in DESC order
            result.add(new WeightListItem(
                    log.getId(),
                    log.getMeasureDate(),
                    log.getMeasureTime(),
                    log.getWeightKg(),
                    log.getNote(),
                    diffMap.get(log.getId())
            ));
        }
        return result;
    }

    /**
     * 取得指定日期區間的體重趨勢圖資料點集合。
     */
    @Transactional(readOnly = true)
    public ChartResponse getChart(LocalDate startDate, LocalDate endDate) {
        CclemonUser user = getCurrentUser();
        List<UserWeightLog> logs = userWeightLogRepository
                .findByUserAndMeasureDateBetweenAndDeletedFalseOrderByMeasureDateDescMeasureTimeDesc(user, startDate, endDate);

        List<ChartPoint> points = new ArrayList<>();
        logs.stream()
                .sorted(Comparator.comparing(UserWeightLog::getMeasureDate))
                .forEach(log -> points.add(new ChartPoint(log.getMeasureDate(), log.getWeightKg())));

        ChartResponse response = new ChartResponse();
        response.setPoints(points);
        return response;
    }

    /**
     * 將指定 ID 的體重紀錄標記為已刪除（軟刪除）。
     */
    @Transactional
    public void softDelete(Long id) {
        UserWeightLog log = userWeightLogRepository.findById(id).orElseThrow();
        log.setDeleted(true);
        userWeightLogRepository.save(log);
    }

    /**
     * 檢查目前登入使用者在指定日期是否已有體重紀錄。
     */
    @Transactional(readOnly = true)
    public boolean hasRecord(LocalDate date) {
        CclemonUser user = getCurrentUser();
        return userWeightLogRepository.existsByUserAndMeasureDateAndDeletedFalse(user, date);
    }

    @lombok.Value
    public static class WeightListItem {
        Long id;
        LocalDate measureDate;
        LocalTime measureTime;
        BigDecimal weightKg;
        String note;
        Double diffFromPrev;
    }

    @lombok.Value
    public static class ChartPoint {
        LocalDate date;
        BigDecimal weightKg;
    }

    @lombok.Data
    public static class ChartResponse {
        private List<ChartPoint> points;
    }
}
