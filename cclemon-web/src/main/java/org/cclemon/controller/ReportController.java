package org.cclemon.controller;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.ReportHandler;
import org.cclemon.api.vo.ScoreResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportHandler reportHandler;

    private Long getCurrentUserId() {
        return 1L;
    }

    @GetMapping("/weekly")
    public List<ScoreResult> getWeekly() {
        return reportHandler.getWeekly(getCurrentUserId());
    }

    @GetMapping("/monthly")
    public List<ScoreResult> getMonthly() {
        return reportHandler.getMonthly(getCurrentUserId());
    }
}
