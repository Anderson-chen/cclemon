package org.cclemon.controller;

import lombok.RequiredArgsConstructor;
import org.cclemon.api.GoalsHandler;
import org.cclemon.api.vo.GoalsResult;
import org.cclemon.api.vo.command.UpdateGoalsCommand;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalsController {

    private final GoalsHandler goalsHandler;

    private Long getCurrentUserId() {
        return 1L;
    }

    @GetMapping
    public GoalsResult getGoals() {
        return goalsHandler.getGoals(getCurrentUserId());
    }

    @PatchMapping
    public GoalsResult updateGoals(@RequestBody UpdateGoalsRequest request) {
        return goalsHandler.updateGoals(UpdateGoalsCommand.builder()
                .userId(getCurrentUserId())
                .dailyCalories(request.dailyCalories())
                .weeklyExerciseDays(request.weeklyExerciseDays())
                .dailyWaterMl(request.dailyWaterMl())
                .targetWeight(request.targetWeight())
                .build());
    }

    record UpdateGoalsRequest(
            Integer dailyCalories,
            Integer weeklyExerciseDays,
            Integer dailyWaterMl,
            BigDecimal targetWeight
    ) {}
}
