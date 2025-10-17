package org.cclemon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.DailyInfo;
import org.cclemon.entity.HealthReport;
import org.cclemon.repository.DailyInfoRepository;
import org.cclemon.repository.HealthReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class HealthReportService {

    private final HealthReportRepository healthReportRepository;
    private final DailyInfoRepository dailyInfoRepository;

    /**
     * 獲取用戶的所有健康報告
     */
    public Page<HealthReport> getUserHealthReports(CclemonUser user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "reportDate"));
        return healthReportRepository.findByUserAndDeletedFalseOrderByReportDateDesc(user, pageable);
    }

    /**
     * 根據類型獲取健康報告
     */
    public List<HealthReport> getReportsByType(CclemonUser user, String reportType) {
        return healthReportRepository.findByUserAndReportTypeAndDeletedFalseOrderByReportDateDesc(user, reportType);
    }

    /**
     * 根據日期範圍獲取健康報告
     */
    public List<HealthReport> getReportsByDateRange(CclemonUser user, String startDate, String endDate) {
        return healthReportRepository.findByUserAndDateRangeAndDeletedFalse(user, startDate, endDate);
    }

    /**
     * 獲取最新報告
     */
    public Optional<HealthReport> getLatestReport(CclemonUser user) {
        return healthReportRepository.findLatestReportByUser(user);
    }

    /**
     * 獲取平均健康分數
     */
    public Double getAverageHealthScore(CclemonUser user) {
        return healthReportRepository.findAverageHealthScoreByUser(user);
    }

    /**
     * 生成健康報告
     */
    @Transactional
    public HealthReport generateHealthReport(CclemonUser user, String reportType, String startDate, String endDate) {
        // 檢查是否已存在相同時期的報告
        Optional<HealthReport> existingReport = healthReportRepository
                .findByUserAndReportTypeAndDateRangeStartAndDateRangeEndAndDeletedFalse(
                        user, reportType, startDate, endDate);
        
        if (existingReport.isPresent()) {
            log.info("報告已存在，返回現有報告: 用戶={}, 類型={}", user.getId(), reportType);
            return existingReport.get();
        }

        // 獲取期間內的健康數據
        List<DailyInfo> healthData = dailyInfoRepository.findByUserAndDateBetween(user, startDate, endDate);
        
        // 創建新報告
        HealthReport report = new HealthReport();
        report.setUser(user);
        report.setReportDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        report.setReportType(reportType);
        report.setDateRangeStart(startDate);
        report.setDateRangeEnd(endDate);
        report.setTitle(generateReportTitle(reportType, startDate, endDate));
        report.setDeleted(false);
        report.setReportStatus("generated");

        // 分析健康數據並填充報告
        analyzeHealthData(report, healthData);

        log.info("生成健康報告: 用戶={}, 類型={}, 時期={}-{}", user.getId(), reportType, startDate, endDate);
        return healthReportRepository.save(report);
    }

    /**
     * 更新健康報告
     */
    @Transactional
    public HealthReport updateHealthReport(Long reportId, HealthReport updatedReport) {
        Optional<HealthReport> existingReport = healthReportRepository.findById(reportId);
        if (existingReport.isEmpty() || existingReport.get().getDeleted()) {
            throw new RuntimeException("健康報告不存在: " + reportId);
        }

        HealthReport report = existingReport.get();
        updateReportFields(report, updatedReport);

        log.info("更新健康報告: ID={}, 標題={}", reportId, report.getTitle());
        return healthReportRepository.save(report);
    }

    /**
     * 刪除健康報告（軟刪除）
     */
    @Transactional
    public void deleteHealthReport(Long reportId) {
        Optional<HealthReport> report = healthReportRepository.findById(reportId);
        if (report.isPresent() && !report.get().getDeleted()) {
            report.get().setDeleted(true);
            report.get().setReportStatus("archived");
            healthReportRepository.save(report.get());
            log.info("刪除健康報告: ID={}", reportId);
        }
    }

    /**
     * 標記報告為已審閱
     */
    @Transactional
    public HealthReport markAsReviewed(Long reportId) {
        Optional<HealthReport> report = healthReportRepository.findById(reportId);
        if (report.isEmpty() || report.get().getDeleted()) {
            throw new RuntimeException("健康報告不存在: " + reportId);
        }

        report.get().setReportStatus("reviewed");
        log.info("標記報告已審閱: ID={}", reportId);
        return healthReportRepository.save(report.get());
    }

    /**
     * 根據類型獲取報告統計
     */
    public long getReportCountByType(CclemonUser user, String reportType) {
        return healthReportRepository.countByUserAndReportTypeAndDeletedFalse(user, reportType);
    }

    /**
     * 生成報告標題
     */
    private String generateReportTitle(String reportType, String startDate, String endDate) {
        String typeLabel = switch (reportType) {
            case "weekly" -> "週報";
            case "monthly" -> "月報";
            case "quarterly" -> "季報";
            case "annual" -> "年報";
            default -> "健康報告";
        };
        return String.format("%s (%s - %s)", typeLabel, startDate, endDate);
    }

    /**
     * 分析健康數據並填充報告
     */
    private void analyzeHealthData(HealthReport report, List<DailyInfo> healthData) {
        if (healthData.isEmpty()) {
            report.setSummary("此期間無健康數據記錄");
            report.setHealthScore(0.0);
            report.setHealthGrade("F");
            return;
        }

        // 體重分析
        analyzeWeight(report, healthData);
        
        // 血壓分析
        analyzeBloodPressure(report, healthData);
        
        // 血糖分析
        analyzeBloodSugar(report, healthData);
        
        // 計算總體健康分數
        calculateHealthScore(report);
        
        // 生成摘要
        generateSummary(report, healthData);
    }

    /**
     * 體重分析
     */
    private void analyzeWeight(HealthReport report, List<DailyInfo> healthData) {
        List<Long> weights = healthData.stream()
                .filter(data -> data.getWeight() != null)
                .map(DailyInfo::getWeight)
                .toList();

        if (!weights.isEmpty()) {
            double avgWeight = weights.stream().mapToLong(Long::longValue).average().orElse(0);
            report.setAvgWeight(avgWeight);

            if (weights.size() > 1) {
                long firstWeight = weights.get(weights.size() - 1);
                long lastWeight = weights.get(0);
                double change = lastWeight - firstWeight;
                report.setWeightChange(change);

                if (Math.abs(change) < 0.5) {
                    report.setWeightTrend("stable");
                } else if (change > 0) {
                    report.setWeightTrend("increasing");
                } else {
                    report.setWeightTrend("decreasing");
                }
            }
        }
    }

    /**
     * 血壓分析
     */
    private void analyzeBloodPressure(HealthReport report, List<DailyInfo> healthData) {
        List<DailyInfo> bpData = healthData.stream()
                .filter(data -> data.getSystolicPressure() != null && data.getDiastolicPressure() != null)
                .toList();

        if (!bpData.isEmpty()) {
            double avgSystolic = bpData.stream().mapToLong(DailyInfo::getSystolicPressure).average().orElse(0);
            double avgDiastolic = bpData.stream().mapToLong(DailyInfo::getDiastolicPressure).average().orElse(0);
            
            report.setAvgSystolic(avgSystolic);
            report.setAvgDiastolic(avgDiastolic);

            // 血壓狀態判定
            if (avgSystolic >= 140 || avgDiastolic >= 90) {
                report.setBloodPressureStatus("high");
            } else if (avgSystolic < 90 || avgDiastolic < 60) {
                report.setBloodPressureStatus("low");
            } else {
                report.setBloodPressureStatus("normal");
            }
        }
    }

    /**
     * 血糖分析
     */
    private void analyzeBloodSugar(HealthReport report, List<DailyInfo> healthData) {
        List<Long> bloodSugars = healthData.stream()
                .filter(data -> data.getBloodSugar() != null)
                .map(DailyInfo::getBloodSugar)
                .toList();

        if (!bloodSugars.isEmpty()) {
            double avgBloodSugar = bloodSugars.stream().mapToLong(Long::longValue).average().orElse(0);
            report.setAvgBloodSugar(avgBloodSugar);

            // 血糖狀態判定（簡化標準）
            if (avgBloodSugar >= 126) {
                report.setBloodSugarStatus("high");
            } else if (avgBloodSugar < 70) {
                report.setBloodSugarStatus("low");
            } else {
                report.setBloodSugarStatus("normal");
            }
        }
    }

    /**
     * 計算健康分數
     */
    private void calculateHealthScore(HealthReport report) {
        double score = 100.0;
        
        // 血壓評分
        if ("high".equals(report.getBloodPressureStatus())) {
            score -= 30;
        } else if ("low".equals(report.getBloodPressureStatus())) {
            score -= 15;
        }
        
        // 血糖評分
        if ("high".equals(report.getBloodSugarStatus())) {
            score -= 25;
        } else if ("low".equals(report.getBloodSugarStatus())) {
            score -= 10;
        }
        
        // 體重趨勢評分（這裡簡化處理）
        if ("increasing".equals(report.getWeightTrend())) {
            score -= 10;
        }
        
        report.setHealthScore(Math.max(0, score));
        
        // 健康等級
        if (score >= 90) {
            report.setHealthGrade("A");
        } else if (score >= 80) {
            report.setHealthGrade("B");
        } else if (score >= 70) {
            report.setHealthGrade("C");
        } else if (score >= 60) {
            report.setHealthGrade("D");
        } else {
            report.setHealthGrade("F");
        }
    }

    /**
     * 生成摘要
     */
    private void generateSummary(HealthReport report, List<DailyInfo> healthData) {
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("分析期間共有 %d 筆健康記錄。", healthData.size()));
        
        if (report.getAvgWeight() != null) {
            summary.append(String.format(" 平均體重: %.1f kg", report.getAvgWeight()));
            if (report.getWeightChange() != null) {
                summary.append(String.format("，體重變化: %+.1f kg", report.getWeightChange()));
            }
        }
        
        if (report.getAvgSystolic() != null) {
            summary.append(String.format(" 平均血壓: %.0f/%.0f mmHg (%s)", 
                report.getAvgSystolic(), report.getAvgDiastolic(), report.getBloodPressureStatus()));
        }
        
        if (report.getAvgBloodSugar() != null) {
            summary.append(String.format(" 平均血糖: %.0f mg/dL (%s)", 
                report.getAvgBloodSugar(), report.getBloodSugarStatus()));
        }
        
        summary.append(String.format(" 整體健康評分: %.0f 分 (%s級)", 
            report.getHealthScore(), report.getHealthGrade()));
        
        report.setSummary(summary.toString());
    }

    /**
     * 更新報告字段
     */
    private void updateReportFields(HealthReport existing, HealthReport updated) {
        if (updated.getTitle() != null) existing.setTitle(updated.getTitle());
        if (updated.getSummary() != null) existing.setSummary(updated.getSummary());
        if (updated.getReportStatus() != null) existing.setReportStatus(updated.getReportStatus());
        if (updated.getRecommendations() != null) existing.setRecommendations(updated.getRecommendations());
        if (updated.getAchievements() != null) existing.setAchievements(updated.getAchievements());
    }
}