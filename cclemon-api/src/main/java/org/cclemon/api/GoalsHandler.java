package org.cclemon.api;

import org.cclemon.api.vo.GoalsResult;
import org.cclemon.api.vo.command.UpdateGoalsCommand;

/**
 * 目標設定 Handler 介面
 */
public interface GoalsHandler {

    GoalsResult getGoals(Long userId);

    GoalsResult updateGoals(UpdateGoalsCommand command);
}
