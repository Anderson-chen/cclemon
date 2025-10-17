package org.cclemon.controller.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cclemon.entity.DailyInfo;
import org.cclemon.service.DailyInfoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/daily-info")
@AllArgsConstructor
@CrossOrigin
public class DailyInfoController {

    private final DailyInfoService dailyInfoService;

    /**
     * 獲取健康記錄列表（分頁）
     */
    @GetMapping
    public ResponseEntity<Page<DailyInfo>> getDailyInfoList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<DailyInfo> result = dailyInfoService.findByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * 根據ID獲取健康記錄
     */
    @GetMapping("/{id}")
    public ResponseEntity<DailyInfo> getDailyInfo(@PathVariable Long id) {
        Optional<DailyInfo> dailyInfo = dailyInfoService.findById(id);
        return dailyInfo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根據日期獲取健康記錄
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<DailyInfo> getDailyInfoByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        Optional<DailyInfo> dailyInfo = dailyInfoService.findByDate(date);
        return dailyInfo.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 創建健康記錄
     */
    @PostMapping
    public ResponseEntity<DailyInfo> createDailyInfo(@RequestBody DailyInfo dailyInfo) {
        try {
            DailyInfo saved = dailyInfoService.save(dailyInfo);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("創建健康記錄失敗", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新健康記錄
     */
    @PutMapping("/{id}")
    public ResponseEntity<DailyInfo> updateDailyInfo(
            @PathVariable Long id, 
            @RequestBody DailyInfo dailyInfo) {
        try {
            dailyInfo.setId(id);
            DailyInfo updated = dailyInfoService.save(dailyInfo);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("更新健康記錄失敗", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 刪除健康記錄
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDailyInfo(@PathVariable Long id) {
        try {
            dailyInfoService.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("刪除健康記錄失敗", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 獲取健康統計資料
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getHealthStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Map<String, Object> stats = dailyInfoService.getHealthStats(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * 獲取圖表資料
     */
    @GetMapping("/chart")
    public ResponseEntity<List<Map<String, Object>>> getHealthChartData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "all") String type) {
        
        List<Map<String, Object>> chartData = dailyInfoService.getChartData(startDate, endDate, type);
        return ResponseEntity.ok(chartData);
    }
}