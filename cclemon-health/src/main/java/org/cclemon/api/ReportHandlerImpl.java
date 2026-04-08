package org.cclemon.api;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.vo.ScoreResult;
import org.cclemon.service.ScoreService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReportHandlerImpl implements ReportHandler {

    private final ScoreService scoreService;

    @Override
    @Transactional(readOnly = true)
    public List<ScoreResult> getWeekly(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(6);
        return scoreService.scoreForRange(userId, from, today);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScoreResult> getMonthly(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate from = today.with(TemporalAdjusters.firstDayOfMonth());
        return scoreService.scoreForRange(userId, from, today);
    }
}
