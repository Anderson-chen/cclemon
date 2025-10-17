package org.cclemon.service;

import lombok.AllArgsConstructor;
import org.cclemon.entity.DailyInfo;
import org.cclemon.repository.DailyInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class DailyInfoService {

    private final DailyInfoRepository dailyInfoRepository;

    public Page<DailyInfo> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (startDate != null && endDate != null) {
            return dailyInfoRepository.findByDateBetweenOrderByDateDesc(
                startDate.toString(), endDate.toString(), pageable);
        } else if (startDate != null) {
            return dailyInfoRepository.findByDateGreaterThanEqualOrderByDateDesc(
                startDate.toString(), pageable);
        } else if (endDate != null) {
            return dailyInfoRepository.findByDateLessThanEqualOrderByDateDesc(
                endDate.toString(), pageable);
        } else {
            return dailyInfoRepository.findAllByOrderByDateDesc(pageable);
        }
    }

    public Optional<DailyInfo> findById(Long id) {
        return dailyInfoRepository.findById(id);
    }

    public Optional<DailyInfo> findByDate(LocalDate date) {
        return dailyInfoRepository.findByDate(date.toString());
    }

    public DailyInfo save(DailyInfo dailyInfo) {
        return dailyInfoRepository.save(dailyInfo);
    }

    public void deleteById(Long id) {
        dailyInfoRepository.deleteById(id);
    }

    public Map<String, Object> getHealthStats(LocalDate startDate, LocalDate endDate) {
        List<DailyInfo> records;
        
        if (startDate != null && endDate != null) {
            records = dailyInfoRepository.findByDateBetweenOrderByDateDesc(
                startDate.toString(), endDate.toString());
        } else {
            // 預設取最近30天的資料
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
            records = dailyInfoRepository.findByDateGreaterThanEqualOrderByDateDesc(
                thirtyDaysAgo.toString());
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", records.size());

        if (!records.isEmpty()) {
            // 計算平均體重
            OptionalDouble avgWeight = records.stream()
                .filter(r -> r.getWeight() != null)
                .mapToLong(DailyInfo::getWeight)
                .average();
            if (avgWeight.isPresent()) {
                stats.put("avgWeight", avgWeight.getAsDouble());
            }

            // 最新體重
            DailyInfo latest = records.get(0);
            if (latest.getWeight() != null) {
                stats.put("recentWeight", latest.getWeight());
            }

            // 體重趨勢分析（簡單比較最近兩筆記錄）
            if (records.size() >= 2 && latest.getWeight() != null) {
                DailyInfo previous = records.get(1);
                if (previous.getWeight() != null) {
                    long diff = latest.getWeight() - previous.getWeight();
                    if (diff > 0) {
                        stats.put("weightTrend", "up");
                    } else if (diff < 0) {
                        stats.put("weightTrend", "down");
                    } else {
                        stats.put("weightTrend", "stable");
                    }
                }
            }
        }

        return stats;
    }

    public List<Map<String, Object>> getChartData(LocalDate startDate, LocalDate endDate, String type) {
        List<DailyInfo> records;
        
        if (startDate != null && endDate != null) {
            records = dailyInfoRepository.findByDateBetweenOrderByDateAsc(
                startDate.toString(), endDate.toString());
        } else {
            // 預設取最近30天的資料
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
            records = dailyInfoRepository.findByDateGreaterThanEqualOrderByDateAsc(
                thirtyDaysAgo.toString());
        }

        List<Map<String, Object>> chartData = new ArrayList<>();
        
        for (DailyInfo record : records) {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("date", record.getDate());
            
            if ("weight".equals(type) || "all".equals(type)) {
                if (record.getWeight() != null) {
                    dataPoint.put("weight", record.getWeight());
                }
            }
            
            // 可以擴展支援血壓、血糖等資料
            // if ("blood-pressure".equals(type) || "all".equals(type)) { ... }
            // if ("blood-sugar".equals(type) || "all".equals(type)) { ... }
            
            if (!dataPoint.isEmpty() && dataPoint.size() > 1) { // 除了date之外還有其他資料
                chartData.add(dataPoint);
            }
        }

        return chartData;
    }
}