package org.cclemon.controller;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.WaterHandler;
import org.cclemon.api.vo.WaterEntryResult;
import org.cclemon.api.vo.command.LogWaterCommand;
import org.cclemon.api.vo.query.WaterQuery;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/water")
@RequiredArgsConstructor
public class WaterController {

    private final WaterHandler waterHandler;

    private Long getCurrentUserId() {
        return 1L;
    }

    @GetMapping
    public WaterEntryResult getToday(@RequestParam(value = "date", required = false) String date) {
        String effectiveDate = date != null ? date : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return waterHandler.getToday(WaterQuery.builder()
                .userId(getCurrentUserId())
                .date(effectiveDate)
                .build());
    }

    @PostMapping("/log")
    public WaterEntryResult logWater(@RequestBody LogWaterRequest request) {
        String date = request.date() != null ? request.date() : LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return waterHandler.logWater(LogWaterCommand.builder()
                .userId(getCurrentUserId())
                .date(date)
                .ml(request.ml())
                .build());
    }

    record LogWaterRequest(Integer ml, String date) {}
}
