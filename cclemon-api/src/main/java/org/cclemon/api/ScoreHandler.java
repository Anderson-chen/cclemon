package org.cclemon.api;

import org.cclemon.api.vo.ScoreResult;
import org.cclemon.api.vo.query.ScoreHistoryQuery;

import java.util.List;

/**
 * 分數計算 Handler 介面
 */
public interface ScoreHandler {

    ScoreResult getToday(Long userId);

    List<ScoreResult> getHistory(ScoreHistoryQuery query);
}
