package org.cclemon.controller.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.cclemon.entity.UserWeightLog;
import org.cclemon.service.WeightService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@RequestMapping("/api/v1/weights")
@AllArgsConstructor
@Validated
public class WeightController {

    private static final int DEFAULT_LIST_LIMIT = 30;
    private static final int DEFAULT_LIST_DAYS = 30;
    private static final int RANGE_7D_DAYS = 7;
    private static final int RANGE_30D_DAYS = 30;
    private static final int RANGE_90D_DAYS = 90;

    private final WeightService weightService;

    /**
     * 建立或更新指定日期的體重紀錄（同一 user + date + source 下為 upsert 行為）。
     * <p>
     * - 若未指定 measureDate，預設為今日。
     * - 若 measureDate 在未來，回傳 400 Bad Request。
     */
    @PostMapping
    public ResponseEntity<UserWeightLogResponse> upsertWeight(@Valid @RequestBody UpsertWeightRequest request) {
        LocalDate today = LocalDate.now();
        LocalDate measureDate = request.getMeasureDate() != null ? request.getMeasureDate() : today;

        if (measureDate.isAfter(today)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        UserWeightLog saved = weightService.upsertManualWeight(
                measureDate,
                request.getWeightKg(),
                request.getMeasureTime(),
                request.getNote()
        );

        return ResponseEntity.ok(UserWeightLogResponse.from(saved));
    }

    /**
     * 查詢一段日期區間內的體重紀錄，預設為最近 30 天，並限制最多回傳 30 筆。
     */
    @GetMapping
    public List<WeightService.WeightListItem> listWeights(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "limit", required = false, defaultValue = "30") int limit
    ) {
        LocalDate today = LocalDate.now();
        if (endDate == null) {
            endDate = today;
        }
        if (startDate == null) {
            startDate = endDate.minusDays(DEFAULT_LIST_DAYS);
        }

        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        if (days > limit) {
            startDate = endDate.minusDays(limit - 1L);
        }

        return weightService.listWeights(startDate, endDate);
    }

    /**
     * 取得體重趨勢圖資料點集合。
     * <p>
     * range 支援：7d、30d、90d、custom（custom 需搭配 startDate 與 endDate）。
     */
    @GetMapping("/chart")
    public WeightService.ChartResponse getChart(
            @RequestParam(value = "range", required = false, defaultValue = "30d") String range,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        LocalDate today = LocalDate.now();
        if ("7d".equals(range)) {
            endDate = today;
            startDate = today.minusDays(RANGE_7D_DAYS);
        } else if ("30d".equals(range)) {
            endDate = today;
            startDate = today.minusDays(RANGE_30D_DAYS);
        } else if ("90d".equals(range)) {
            endDate = today;
            startDate = today.minusDays(RANGE_90D_DAYS);
        } else if ("custom".equals(range)) {
            if (startDate == null || endDate == null) {
                throw new IllegalArgumentException("startDate and endDate are required when range=custom");
            }
        } else {
            throw new IllegalArgumentException("Unsupported range: " + range);
        }

        return weightService.getChart(startDate, endDate);
    }

    /**
     * 軟刪除指定 ID 的體重紀錄。
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        weightService.softDelete(id);
    }

    /**
     * 檢查指定日期是否已有體重紀錄，供通知模組判斷是否需要提醒。
     */
    @GetMapping("/has-record")
    public HasRecordResponse hasRecord(@RequestParam("date")
                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean hasRecord = weightService.hasRecord(date);
        HasRecordResponse resp = new HasRecordResponse();
        resp.setDate(date);
        resp.setHasRecord(hasRecord);
        return resp;
    }

    @lombok.Data
    public static class UpsertWeightRequest {

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate measureDate;

        @NotNull
        @Min(20)
        @Max(300)
        private BigDecimal weightKg;

        @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
        private LocalTime measureTime;

        private String note;
    }

    @lombok.Value
    public static class UserWeightLogResponse {
        Long id;
        LocalDate measureDate;
        LocalTime measureTime;
        BigDecimal weightKg;
        String note;

        public static UserWeightLogResponse from(UserWeightLog log) {
            return new UserWeightLogResponse(
                    log.getId(),
                    log.getMeasureDate(),
                    log.getMeasureTime(),
                    log.getWeightKg(),
                    log.getNote()
            );
        }
    }

    @lombok.Data
    public static class HasRecordResponse {
        private LocalDate date;
        private boolean hasRecord;
    }
}
