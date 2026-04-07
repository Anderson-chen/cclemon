package org.cclemon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.cclemon.api.WeightHandler;
import org.cclemon.api.vo.WeightChartData;
import org.cclemon.api.vo.WeightRecordResult;
import org.cclemon.api.vo.command.DeleteWeightCommand;
import org.cclemon.api.vo.command.RecordWeightCommand;
import org.cclemon.api.vo.query.GetWeightChartQuery;
import org.cclemon.api.vo.query.ListWeightsQuery;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/weight")
@RequiredArgsConstructor
@Validated
public class WeightController {

    private final WeightHandler weightHandler;

    // TODO: Get current user ID from security context
    private Long getCurrentUserId() {
        return 1L;
    }

    @PostMapping
    public ResponseEntity<WeightResponse> upsertWeight(@Valid @RequestBody WeightUpsertRequest request) {
        LocalDate today = LocalDate.now();
        LocalDate measureDate = request.getDate() != null ? request.getDate() : today;

        if (measureDate.isAfter(today)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        RecordWeightCommand command = RecordWeightCommand.builder()
                .userId(getCurrentUserId())
                .measureDate(measureDate)
                .weightKg(request.getWeight())
                .measureTime(request.getMeasureTime())
                .note(request.getNote())
                .build();

        WeightRecordResult result = weightHandler.record(command);
        return ResponseEntity.ok(toResponse(result));
    }

    @GetMapping
    public List<WeightResponse> listWeights(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "90") int size
    ) {
        LocalDate finalEndDate = (endDate == null) ? LocalDate.now() : endDate;
        LocalDate finalStartDate = (startDate == null) ? finalEndDate.minusDays(89) : startDate;

        ListWeightsQuery query = ListWeightsQuery.builder()
                .userId(getCurrentUserId())
                .startDate(finalStartDate)
                .endDate(finalEndDate)
                .page(page)
                .size(size)
                .build();

        return weightHandler.list(query).getContent()
                .stream().map(this::toResponse).toList();
    }

    private WeightResponse toResponse(WeightRecordResult result) {
        return new WeightResponse(
                String.valueOf(result.getLogId()),
                String.valueOf(result.getUserId()),
                result.getMeasureDate() != null ? result.getMeasureDate().toString() : null,
                result.getWeightKg() != null ? result.getWeightKg().doubleValue() : null,
                null
        );
    }

    record WeightResponse(String id, String userId, String date, Double weight, Object measurements) {}

    @GetMapping("/chart")
    public WeightChartData getChart(
            @RequestParam(value = "range", defaultValue = "30d") String range,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        LocalDate finalEndDate;
        LocalDate finalStartDate;
        LocalDate today = LocalDate.now();

        switch (range) {
            case "7d":
                finalEndDate = today;
                finalStartDate = today.minusDays(6);
                break;
            case "90d":
                finalEndDate = today;
                finalStartDate = today.minusDays(89);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    throw new IllegalArgumentException("startDate and endDate are required for custom range.");
                }
                finalStartDate = startDate;
                finalEndDate = endDate;
                break;
            case "30d":
            default:
                finalEndDate = today;
                finalStartDate = today.minusDays(29);
                break;
        }

        GetWeightChartQuery query = GetWeightChartQuery.builder()
                .userId(getCurrentUserId())
                .startDate(finalStartDate)
                .endDate(finalEndDate)
                .build();

        return weightHandler.getChart(query);
    }



    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        DeleteWeightCommand command = DeleteWeightCommand.builder()
                .userId(getCurrentUserId())
                .logId(id)
                .build();
        weightHandler.delete(command);
    }
}
