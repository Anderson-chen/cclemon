package org.cclemon.api;

import org.cclemon.api.vo.ScoreResult;

import java.util.List;

/**
 * 報告 Handler 介面
 */
public interface ReportHandler {

    List<ScoreResult> getWeekly(Long userId);

    List<ScoreResult> getMonthly(Long userId);
}
