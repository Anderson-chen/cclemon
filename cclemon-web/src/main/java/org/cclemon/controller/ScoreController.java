package org.cclemon.controller;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.ScoreHandler;
import org.cclemon.api.vo.ScoreResult;
import org.cclemon.api.vo.query.ScoreHistoryQuery;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreHandler scoreHandler;

    private Long getCurrentUserId() {
        return 1L;
    }

    @GetMapping("/today")
    public ScoreResult getToday() {
        return scoreHandler.getToday(getCurrentUserId());
    }

    @GetMapping("/history")
    public List<ScoreResult> getHistory(@RequestParam(value = "days", defaultValue = "30") int days) {
        return scoreHandler.getHistory(ScoreHistoryQuery.builder()
                .userId(getCurrentUserId())
                .days(days)
                .build());
    }
}
