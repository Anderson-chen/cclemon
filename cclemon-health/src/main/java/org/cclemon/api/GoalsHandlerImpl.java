package org.cclemon.api;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.vo.GoalsResult;
import org.cclemon.api.vo.command.UpdateGoalsCommand;
import org.cclemon.entity.UserGoalSetting;
import org.cclemon.service.GoalsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GoalsHandlerImpl implements GoalsHandler {

    private final GoalsService goalsService;

    @Override
    @Transactional(readOnly = true)
    public GoalsResult getGoals(Long userId) {
        return toResult(goalsService.getOrDefault(userId));
    }

    @Override
    @Transactional
    public GoalsResult updateGoals(UpdateGoalsCommand command) {
        UserGoalSetting setting = goalsService.updateGoals(
                command.getUserId(),
                command.getDailyCalories(),
                command.getWeeklyExerciseDays(),
                command.getDailyWaterMl(),
                command.getTargetWeight()
        );
        return toResult(setting);
    }

    private GoalsResult toResult(UserGoalSetting setting) {
        return GoalsResult.builder()
                .dailyCalories(setting.getDailyCalories())
                .weeklyExerciseDays(setting.getWeeklyExerciseDays())
                .dailyWaterMl(setting.getDailyWaterMl())
                .targetWeight(setting.getTargetWeight())
                .build();
    }
}
