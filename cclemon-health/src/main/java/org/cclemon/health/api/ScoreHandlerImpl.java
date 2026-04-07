package org.cclemon.health.api;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.ScoreHandler;
import org.cclemon.api.vo.ScoreResult;
import org.cclemon.api.vo.query.ScoreHistoryQuery;
import org.cclemon.health.internal.service.ScoreService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScoreHandlerImpl implements ScoreHandler {

    private final ScoreService scoreService;

    @Override
    @Transactional(readOnly = true)
    public ScoreResult getToday(Long userId) {
        return scoreService.scoreForDate(userId, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScoreResult> getHistory(ScoreHistoryQuery query) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(query.getDays() - 1);
        return scoreService.scoreForRange(query.getUserId(), from, to);
    }
}
