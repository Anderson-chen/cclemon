package org.cclemon.api.vo;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class GoalsResult {
    Integer dailyCalories;
    Integer weeklyExerciseDays;
    Integer dailyWaterMl;
    BigDecimal targetWeight;
}
