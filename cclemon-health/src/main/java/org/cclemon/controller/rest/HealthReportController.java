package org.cclemon.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.CclemonUser;
import org.cclemon.entity.HealthReport;
import org.cclemon.service.CclemonUserService;
import org.cclemon.service.HealthReportService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/health-reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class HealthReportController {

    private final HealthReportService healthReportService;
    private final CclemonUserService userService;

    /**
     * 獲取用戶的所有健康報告
     */
    @GetMapping
    public ResponseEntity<Page<HealthReport>> getUserHealthReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            Page<HealthReport> reports = healthReportService.getUserHealthReports(currentUser, page, size);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            log.error("獲取用戶健康報告失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據類型獲取健康報告
     */
    @GetMapping("/type/{reportType}")
    public ResponseEntity<List<HealthReport>> getReportsByType(@PathVariable String reportType) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<HealthReport> reports = healthReportService.getReportsByType(currentUser, reportType);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            log.error("根據類型獲取健康報告失敗: {}", reportType, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據日期範圍獲取健康報告
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<HealthReport>> getReportsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            List<HealthReport> reports = healthReportService.getReportsByDateRange(currentUser, startDate, endDate);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            log.error("根據日期範圍獲取健康報告失敗: {} - {}", startDate, endDate, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 獲取最新報告
     */
    @GetMapping("/latest")
    public ResponseEntity<HealthReport> getLatestReport() {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            Optional<HealthReport> report = healthReportService.getLatestReport(currentUser);
            return report.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("獲取最新健康報告失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 獲取平均健康分數
     */
    @GetMapping("/average-score")
    public ResponseEntity<Double> getAverageHealthScore() {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            Double averageScore = healthReportService.getAverageHealthScore(currentUser);
            return ResponseEntity.ok(averageScore != null ? averageScore : 0.0);
        } catch (Exception e) {
            log.error("獲取平均健康分數失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 生成健康報告
     */
    @PostMapping("/generate")
    public ResponseEntity<HealthReport> generateHealthReport(
            @RequestParam String reportType,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            HealthReport report = healthReportService.generateHealthReport(currentUser, reportType, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("生成健康報告失敗: 類型={}, 時期={}-{}", reportType, startDate, endDate, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 生成週報
     */
    @PostMapping("/generate/weekly")
    public ResponseEntity<HealthReport> generateWeeklyReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            HealthReport report = healthReportService.generateHealthReport(currentUser, "weekly", startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("生成週報失敗: 時期={}-{}", startDate, endDate, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 生成月報
     */
    @PostMapping("/generate/monthly")
    public ResponseEntity<HealthReport> generateMonthlyReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            HealthReport report = healthReportService.generateHealthReport(currentUser, "monthly", startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("生成月報失敗: 時期={}-{}", startDate, endDate, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 生成季報
     */
    @PostMapping("/generate/quarterly")
    public ResponseEntity<HealthReport> generateQuarterlyReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            HealthReport report = healthReportService.generateHealthReport(currentUser, "quarterly", startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("生成季報失敗: 時期={}-{}", startDate, endDate, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 生成年報
     */
    @PostMapping("/generate/annual")
    public ResponseEntity<HealthReport> generateAnnualReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            HealthReport report = healthReportService.generateHealthReport(currentUser, "annual", startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("生成年報失敗: 時期={}-{}", startDate, endDate, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 更新健康報告
     */
    @PutMapping("/{id}")
    public ResponseEntity<HealthReport> updateHealthReport(@PathVariable Long id, @RequestBody HealthReport report) {
        try {
            HealthReport updatedReport = healthReportService.updateHealthReport(id, report);
            return ResponseEntity.ok(updatedReport);
        } catch (RuntimeException e) {
            log.error("更新健康報告失敗: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("更新健康報告失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 刪除健康報告
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthReport(@PathVariable Long id) {
        try {
            healthReportService.deleteHealthReport(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("刪除健康報告失敗: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 標記報告為已審閱
     */
    @PatchMapping("/{id}/review")
    public ResponseEntity<HealthReport> markAsReviewed(@PathVariable Long id) {
        try {
            HealthReport report = healthReportService.markAsReviewed(id);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            log.error("標記報告已審閱失敗: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("標記報告已審閱失敗", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 根據類型獲取報告統計
     */
    @GetMapping("/stats/{reportType}")
    public ResponseEntity<Long> getReportCountByType(@PathVariable String reportType) {
        try {
            CclemonUser currentUser = userService.getCurrentUser();
            long count = healthReportService.getReportCountByType(currentUser, reportType);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("獲取報告統計失敗: {}", reportType, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}